package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.Currency;
import com.andriyuk.backendtest.api.v0_1.TransferRequest;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for testing concurrent account operations
 */
@MicronautTest
public class ConcurrencyTest extends AccountServiceTest {

    @Inject
    ConcurrencyTestAccountService accountService;

    private Executor simpleExecutor = (runnable -> new Thread(runnable).start());

    @MicronautTest
    @Test
    public void concurrentTransferTest() throws InterruptedException {
        CountDownLatch transferLatch = new CountDownLatch(1);
        CountDownLatch resultLatch = new CountDownLatch(2);

        BigDecimal transferAmount = BigDecimal.TEN;
        Account sourceAccount = accountService.add(getRandomAccountTemplate(transferAmount, Currency.EUR));
        Account destinationAccount = accountService.add(getRandomAccountTemplate(getRandomBigDecimal(), Currency.EUR));

        TransferRequest request = new TransferRequest(sourceAccount.getId(), destinationAccount.getId(), transferAmount);
        //Do parallel transfer operations
        simpleExecutor.execute(() -> accountService.waitForWithdrawAndTransfer(request, transferLatch, resultLatch));
        simpleExecutor.execute(() -> accountService.withdrawWaitAndDeposit(request, transferLatch, resultLatch));

        resultLatch.await(); //Wait until both parallel transfer operations are completed

        assertEquals(transferAmount.subtract(transferAmount), accountService.getById(sourceAccount.getId()).getBalance(),
                "Non zero account balance means lack of thread safety.");

    }

}
