package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.*;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AccountServiceTest {

    @Inject
    AccountService accountService;

    @MicronautTest
    @Test
    public void addTest() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.add(getRandomAccountTemplate(BigDecimal.ONE.negate())),
                "Client shouldn't be able to add account with negative amount of money.");

        AccountTemplate tooLongNumberAccountTemplate =
                getRandomAccountTemplate(RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH + 1,
                        true, true));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.add(tooLongNumberAccountTemplate),
                "Client shouldn't be able to add account with illegal number.");

        AccountTemplate validAccountTemplate = getRandomAccountTemplate();
        Account createdAccount = accountService.add(validAccountTemplate);
        assertEquals(validAccountTemplate, createdAccount,
                "Created account and template should have same field values (except whose which specific to Account)");
        assertEquals(createdAccount.getState(), AccountState.OPENED, "Created account should be in OPENED state.");
    }

    @MicronautTest
    @Test
    public void getByInvalidIdTest() {
        assertThrows(IllegalArgumentException.class,
                //Account with big negative id definitely doesn't exist
                () -> accountService.getById(BigInteger.valueOf(Long.MIN_VALUE)),
                "Client shouldn't be able to query non existent account.");
    }

    @MicronautTest
    @Test
    public void closeTest() {
        Account closingAccount = accountService.add(getRandomAccountTemplate(BigDecimal.ZERO));
        Account remainUnchangedAccount = accountService.add(getRandomAccountTemplate());

        assertEquals(accountService.getById(closingAccount.getId()).getState(), AccountState.OPENED,
                "Account state should be  \"OPENED\".");

        accountService.close(closingAccount.getId());

        assertEquals(accountService.getById(closingAccount.getId()).getState(), AccountState.CLOSED,
                "Account state should be changed to \"CLOSED\".");

        assertEquals(accountService.getById(remainUnchangedAccount.getId()).getState(), remainUnchangedAccount.getState(),
                "Rest accounts should remain unchanged.");

        assertThrows(IllegalStateException.class,
                () -> accountService.close(closingAccount.getId()),
                "Client shouldn't be able to close already closed account.");

        Account nonEmptyAccount = accountService.add(getRandomAccountTemplate(BigDecimal.TEN));
        assertThrows(IllegalStateException.class,
                () -> accountService.close(nonEmptyAccount.getId()),
                "Client shouldn't be able to close non empty account.");
    }

    @MicronautTest
    @Test
    public void depositTest() {
        Account invalidAccount = accountService.add(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(invalidAccount.getId(), BigDecimal.ZERO),
                "Client shouldn't be able to deposit zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> accountService.deposit(invalidAccount.getId(), BigDecimal.ONE.negate()),
                "Client shouldn't be able to deposit negative amount of money.");

        accountService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> accountService.deposit(invalidAccount.getId(), BigDecimal.ONE),
                "Client shouldn't be able to deposit into closed account.");

        Account originalAccount = accountService.add(getRandomAccountTemplate());
        Account otherAccount = accountService.add(getRandomAccountTemplate());
        BigDecimal depositAmount = getRandomBigDecimal();
        Account changedAccount = accountService.deposit(originalAccount.getId(), depositAmount);
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().add(depositAmount),
                "Account balance should increase by specified amount");

        assertEquals(otherAccount, accountService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }

    @MicronautTest
    @Test
    public void withdrawTest() {
        Account invalidAccount = accountService.add(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(invalidAccount.getId(), BigDecimal.ZERO),
                "Client shouldn't be able to withdraw zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(invalidAccount.getId(), BigDecimal.ONE.negate()),
                "Client shouldn't be able to withdraw negative amount of money.");

        accountService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> accountService.withdraw(invalidAccount.getId(), BigDecimal.ONE),
                "Client shouldn't be able to withdraw from closed account.");

        BigDecimal withdrawAmount = getRandomBigDecimal();
        Account originalAccount = accountService.add(getRandomAccountTemplate(withdrawAmount));
        Account otherAccount = accountService.add(getRandomAccountTemplate());

        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(originalAccount.getId(), withdrawAmount.multiply(BigDecimal.TEN)),
                "Client shouldn't be able to withdraw extra money.");

        Account changedAccount = accountService.withdraw(originalAccount.getId(), withdrawAmount);
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().subtract(withdrawAmount),
                "Account balance should decrease by specified amount");

        assertEquals(otherAccount, accountService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }

    @MicronautTest
    @Test
    public void transferTest() {
        BigDecimal transferAmount = getRandomBigDecimal();
        Account sourceAccount = accountService.add(getRandomAccountTemplate(transferAmount, Currency.EUR));
        Account destinationAccount = accountService.add(getRandomAccountTemplate(getRandomBigDecimal(), Currency.EUR));
        Account otherAccount = accountService.add(getRandomAccountTemplate());

        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.ZERO)),
                "Client shouldn't be able to transfer zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.ONE.negate())),
                "Client shouldn't be able to transfer negative amount of money.");

        Account emptyAccount = accountService.add(getRandomAccountTemplate(BigDecimal.ZERO));
        accountService.close(emptyAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> accountService.transfer(new TransferRequest(emptyAccount.getId(), destinationAccount.getId(), BigDecimal.ONE)),
                "Client shouldn't be able to transfer money from closed account.");
        assertThrows(IllegalStateException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), emptyAccount.getId(), BigDecimal.ONE)),
                "Client shouldn't be able to transfer money to closed account.");

        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), transferAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to transfer extra money.");

        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), transferAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to transfer extra money.");

        Account differentCurrencyAccount = accountService.add(getRandomAccountTemplate(getRandomBigDecimal(), Currency.USD));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(sourceAccount.getId(), differentCurrencyAccount.getId(), transferAmount)),
                "Client shouldn't be able to transfer money between accounts with different currencies.");
        assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(new TransferRequest(differentCurrencyAccount.getId(), sourceAccount.getId(), transferAmount)),
                "Client shouldn't be able to transfer money between accounts with different currencies.");

        accountService.transfer(new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), transferAmount));
        Account sourceAccountChanged = accountService.getById(sourceAccount.getId());
        Account destinationAccountChanged = accountService.getById(destinationAccount.getId());
        assertEquals(destinationAccountChanged.getBalance(), destinationAccount.getBalance().add(transferAmount),
                "Destination account balance should increase by specified amount");
        assertEquals(sourceAccountChanged.getBalance(), sourceAccount.getBalance().subtract(transferAmount),
                "Source account balance should decrease by specified amount");

        assertEquals(otherAccount, accountService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");

        //todo обработка коммиссии!
        //todo счет для списания комиссии(с отрицательным идентификтором)
        //todo рест получения комиссии
        //todo сервис получения коммиссии по номеру счета(в перспективе обычный миросервис). Для своих счетов нулевая комиссиия
        //todo таблица операций с балансом(запрос по пользователю, запрос по счету)
        //todo таблица оперций со счетом(открытие закрытие)
    }

    private BigInteger getRandomBigInteger() {
        return new BigInteger(32, new Random());
    }

    protected BigDecimal getRandomBigDecimal() {
        return new BigDecimal(Math.abs(Math.random()) * 100000);
    }

    private AccountTemplate getRandomAccountTemplate(String number) {
        return new AccountTemplate(getRandomBigInteger(), number, getRandomBigDecimal(), Currency.getRandom());
    }

    private AccountTemplate getRandomAccountTemplate(BigDecimal balance) {
        return getRandomAccountTemplate(balance, Currency.getRandom());
    }

    protected AccountTemplate getRandomAccountTemplate(BigDecimal balance, Currency currency) {
        return new AccountTemplate(getRandomBigInteger(),
                RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH, true, true),
                balance, currency);
    }
    private AccountTemplate getRandomAccountTemplate() {
        return getRandomAccountTemplate(getRandomBigDecimal());
    }
}
