package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static com.andriyuk.backendtest.account.v0_1.service.TestHelper.getRandomAccountTemplate;
import static com.andriyuk.backendtest.account.v0_1.service.TestHelper.getRandomBigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class WithdrawalServiceTest {

    @Inject
    AccountService accountService;

    @Inject
    WithdrawalService withdrawalService;


    //todo split this god test to separate cases
    @MicronautTest
    @Test
    public void withdrawTest() {
        Account invalidAccount = accountService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ZERO)),
                "Client shouldn't be able to create zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE.negate())),
                "Client shouldn't be able to create negative amount of money.");

        accountService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> withdrawalService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create from closed account.");

        BigDecimal withdrawAmount = getRandomBigDecimal();
        Account originalAccount = accountService.create(getRandomAccountTemplate(withdrawAmount));
        Account otherAccount = accountService.create(getRandomAccountTemplate());

        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, withdrawAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to create extra money.");

        withdrawalService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, withdrawAmount));
        Account changedAccount = accountService.getById(originalAccount.getId());
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().subtract(withdrawAmount),
                "Account balance should decrease by specified amount");

        assertEquals(otherAccount, accountService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }
}
