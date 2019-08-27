package com.andriyuk.backendtest.accountservice.v0_1.service.withdrawal;

import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountCommandService;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountQueryService;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static com.andriyuk.backendtest.accountservice.v0_1.service.TestHelper.getRandomAccountTemplate;
import static com.andriyuk.backendtest.accountservice.v0_1.service.TestHelper.getRandomBigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class WithdrawalCommandServiceTest {

    @Inject
    AccountQueryService accountQueryService;
    @Inject
    AccountCommandService accountCommandService;

    @Inject
    WithdrawalCommandService withdrawalCommandService;


    //todo split this god test to separate cases
    @MicronautTest
    @Test
    public void withdrawTest() {
        Account invalidAccount = accountCommandService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ZERO)),
                "Client shouldn't be able to create zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE.negate())),
                "Client shouldn't be able to create negative amount of money.");

        accountCommandService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> withdrawalCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create from closed account.");

        BigDecimal withdrawAmount = getRandomBigDecimal();
        Account originalAccount = accountCommandService.create(getRandomAccountTemplate(withdrawAmount));
        Account otherAccount = accountCommandService.create(getRandomAccountTemplate());

        assertThrows(IllegalArgumentException.class,
                () -> withdrawalCommandService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, withdrawAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to create extra money.");

        withdrawalCommandService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, withdrawAmount));
        Account changedAccount = accountQueryService.getById(originalAccount.getId());
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().subtract(withdrawAmount),
                "Account balance should decrease by specified amount");

        assertEquals(otherAccount, accountQueryService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }
}
