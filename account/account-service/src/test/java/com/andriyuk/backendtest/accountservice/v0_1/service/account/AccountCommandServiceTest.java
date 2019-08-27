package com.andriyuk.backendtest.accountservice.v0_1.service.account;

import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.AccountState;
import com.andriyuk.backendtest.accountapi.v0_1.account.AccountTemplate;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.accountservice.v0_1.service.withdrawal.WithdrawalCommandService;
import io.micronaut.test.annotation.MicronautTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static com.andriyuk.backendtest.accountservice.v0_1.service.TestHelper.getRandomAccountTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class AccountCommandServiceTest {

    @Inject
    AccountQueryService accountQueryService;
    @Inject
    AccountCommandService accountCommandService;

    @Inject
    WithdrawalCommandService withdrawalCommandService;


    @MicronautTest
    @Test
    public void addTest() {
        assertThrows(IllegalArgumentException.class,
                () -> accountCommandService.create(getRandomAccountTemplate(BigDecimal.ONE.negate())),
                "Client shouldn't be able to create account with negative amount of money.");

        AccountTemplate tooLongNumberAccountTemplate =
                getRandomAccountTemplate(RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH + 1,
                        true, true));
        assertThrows(IllegalArgumentException.class,
                () -> accountCommandService.create(tooLongNumberAccountTemplate),
                "Client shouldn't be able to create account with illegal number.");

        AccountTemplate validAccountTemplate = getRandomAccountTemplate();
        Account createdAccount = accountCommandService.create(validAccountTemplate);
        assertEquals(validAccountTemplate, createdAccount,
                "Created account and template should have same field values (except whose which specific to Account)");
        assertEquals(createdAccount.getState(), AccountState.OPENED, "Created account should be in OPENED state.");
    }

    @MicronautTest
    @Test
    public void closeTest() {
        Account closingAccount = accountCommandService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        Account remainUnchangedAccount = accountCommandService.create(getRandomAccountTemplate());

        assertEquals(accountQueryService.getById(closingAccount.getId()).getState(), AccountState.OPENED,
                "Account state should be  \"OPENED\".");

        accountCommandService.close(closingAccount.getId());

        assertEquals(accountQueryService.getById(closingAccount.getId()).getState(), AccountState.CLOSED,
                "Account state should be changed to \"CLOSED\".");

        assertEquals(accountQueryService.getById(remainUnchangedAccount.getId()).getState(), remainUnchangedAccount.getState(),
                "Rest accounts should remain unchanged.");

        assertThrows(IllegalStateException.class,
                () -> accountCommandService.close(closingAccount.getId()),
                "Client shouldn't be able to close already closed account.");

        Account nonEmptyAccount = accountCommandService.create(getRandomAccountTemplate(BigDecimal.TEN));
        assertThrows(IllegalStateException.class,
                () -> accountCommandService.close(nonEmptyAccount.getId()),
                "Client shouldn't be able to close non empty account.");
    }

    @MicronautTest
    @Test
    public void accountChangedCheckTest() {
        Account account = accountCommandService.create(getRandomAccountTemplate(BigDecimal.TEN));
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest(account, BigDecimal.TEN);
        withdrawalCommandService.create(account.getId(), balanceChangeRequest); //after this balanceChangeRequest became outdated, since amount is changed
        assertThrows(IllegalStateException.class, () -> withdrawalCommandService.create(account.getId(), balanceChangeRequest),
                "Error should be thrown on processing outdated request.");
    }

}
