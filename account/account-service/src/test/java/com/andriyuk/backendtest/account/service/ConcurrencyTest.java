package com.andriyuk.backendtest.account.service;

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
    ConcurrentAccountService accountService;

    private Executor executor = (runnable -> new Thread(runnable).start());

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
        executor.execute(() -> accountService.transferWaitForWithdraw(request, transferLatch, resultLatch));
        executor.execute(() -> accountService.transferWithdrawAndWait(request, transferLatch, resultLatch));

        resultLatch.await(); //Wait until both parallel transfer operations are completed

        assertEquals(transferAmount.subtract(transferAmount), accountService.getById(sourceAccount.getId()).getBalance(),
                "Non zero account balance means lack of thread safety.");

    }

}
