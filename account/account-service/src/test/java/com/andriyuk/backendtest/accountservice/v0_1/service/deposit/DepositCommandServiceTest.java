package com.andriyuk.backendtest.accountservice.v0_1.service.deposit;

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
public class DepositCommandServiceTest {

    @Inject
    AccountQueryService accountQueryService;
    @Inject
    AccountCommandService accountCommandService;


    @Inject
    DepositCommandService depositCommandService;


    @MicronautTest
    @Test
    public void depositTest() {
        Account invalidAccount = accountCommandService.create(getRandomAccountTemplate(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> depositCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ZERO)),
                "Client shouldn't be able to create zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> depositCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE.negate())),
                "Client shouldn't be able to create negative amount of money.");

        accountCommandService.close(invalidAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> depositCommandService.create(invalidAccount.getId(), new BalanceChangeRequest(invalidAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create into closed account.");

        Account originalAccount = accountCommandService.create(getRandomAccountTemplate());
        Account otherAccount = accountCommandService.create(getRandomAccountTemplate());
        BigDecimal depositAmount = getRandomBigDecimal();
        depositCommandService.create(originalAccount.getId(), new BalanceChangeRequest(originalAccount, depositAmount));
        Account changedAccount = accountQueryService.getById(originalAccount.getId());
        assertEquals(changedAccount.getBalance(), originalAccount.getBalance().add(depositAmount),
                "Account balance should increase by specified amount");

        assertEquals(otherAccount, accountQueryService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }

}
