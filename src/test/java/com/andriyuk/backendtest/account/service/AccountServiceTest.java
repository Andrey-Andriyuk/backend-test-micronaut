package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountState;
import com.andriyuk.backendtest.api.v0_1.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.Currency;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class AccountServiceTest {

    @Inject
    AccountService accountService;

    @Test
    public void addTest() {
        AccountTemplate tooLongNumberAccountTemplate =
                getRandomAccountTemplate(RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH + 1,
                        true, true));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.add(tooLongNumberAccountTemplate),
                "Shouldn't be able to add account with illegal number.");

        AccountTemplate validAccountTemplate = getRandomAccountTemplate();
        Account createdAccount = accountService.add(validAccountTemplate);
        assertEquals(validAccountTemplate, createdAccount,
                "Created account and template should have same field values (except whose which specific to Account)");
        assertEquals(createdAccount.getState(), AccountState.OPENED, "Created account should be in OPENED state.");
    }

    @Test
    public void getByInvalidIdTest() {
        assertThrows(IllegalArgumentException.class,
                //Account with negative id definitely doesn't exist
                () -> accountService.getById(BigInteger.ONE.negate()),
                "Shouldn't be able to query non existent account.");
    }

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
                "Shouldn't be able to close already closed account.");

        Account nonEmptyAccount = accountService.add(getRandomAccountTemplate(BigDecimal.TEN));
        assertThrows(IllegalStateException.class,
                () -> accountService.close(nonEmptyAccount.getId()),
                "Shouldn't be able to close non empty account.");
    }

    private BigInteger getRandomBigInteger() {
        return new BigInteger(32, new Random());
    }

    private BigDecimal getRandomBigDecimal() {
        return new BigDecimal(Math.abs(Math.random()) * 100000);
    }

    private AccountTemplate getRandomAccountTemplate(String number) {
        return new AccountTemplate(getRandomBigInteger(), number, getRandomBigDecimal(), Currency.getRandom());
    }

    private AccountTemplate getRandomAccountTemplate(BigDecimal balance) {
        return new AccountTemplate(getRandomBigInteger(),
                RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH, true, true),
                balance, Currency.getRandom());
    }

    private AccountTemplate getRandomAccountTemplate() {
        return getRandomAccountTemplate(getRandomBigDecimal());
    }
}
