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
public class DepositServiceTest {

    @Inject
    AccountService accountService;

    @Inject
    DepositService depositService;


    @MicronautTest
    @Test
    public void depositTest() {
        Account invalidAccount = accountService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> depositService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ZERO)),
                "Client shouldn't be able to create zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> depositService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE.negate())),
                "Client shouldn't be able to create negative amount of money.");

        accountService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> depositService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create into closed account.");

        Account originalAccount = accountService.create(getRandomAccountTemplate());
        Account otherAccount = accountService.create(getRandomAccountTemplate());
        BigDecimal depositAmount = getRandomBigDecimal();
        depositService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, depositAmount));
        Account changedAccount = accountService.getById(originalAccount.getId());
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().add(depositAmount),
                "Account balance should increase by specified amount");

        assertEquals(otherAccount, accountService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }

}
