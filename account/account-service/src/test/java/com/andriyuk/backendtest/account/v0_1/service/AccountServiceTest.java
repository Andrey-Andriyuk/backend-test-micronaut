package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.account.*;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferRequest;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.andriyuk.backendtest.account.v0_1.service.TestHelper.getRandomAccountTemplate;
import static com.andriyuk.backendtest.account.v0_1.service.TestHelper.getRandomBigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AccountServiceTest {

    @Inject
    AccountService accountService;

    @Inject
    WithdrawalService withdrawalService;

    @MicronautTest
    @Test
    public void addTest() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.create(getRandomAccountTemplate(BigDecimal.ONE.negate())),
                "Client shouldn't be able to create account with negative amount of money.");

        AccountTemplate tooLongNumberAccountTemplate =
                getRandomAccountTemplate(RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH + 1,
                        true, true));
        assertThrows(IllegalArgumentException.class,
                () -> accountService.create(tooLongNumberAccountTemplate),
                "Client shouldn't be able to create account with illegal number.");

        AccountTemplate validAccountTemplate = getRandomAccountTemplate();
        Account createdAccount = accountService.create(validAccountTemplate);
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
        Account closingAccount = accountService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        Account remainUnchangedAccount = accountService.create(getRandomAccountTemplate());

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

        Account nonEmptyAccount = accountService.create(getRandomAccountTemplate(BigDecimal.TEN));
        assertThrows(IllegalStateException.class,
                () -> accountService.close(nonEmptyAccount.getId()),
                "Client shouldn't be able to close non empty account.");
    }

    @MicronautTest
    @Test
    public void accountChangedCheckTest() {
        Account account = accountService.create(getRandomAccountTemplate(BigDecimal.TEN));
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest(account, BigDecimal.TEN);
        withdrawalService.create(account.getId(), balanceChangeRequest); //after this balanceChangeRequest became outdated, since amount is changed
        assertThrows(IllegalStateException.class, () -> withdrawalService.create(account.getId(), balanceChangeRequest),
                "Error should be thrown on processing outdated request.");
    }
}
