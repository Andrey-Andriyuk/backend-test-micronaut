package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.TransferRequest;
import com.andriyuk.backendtest.api.v0_1.TransferResult;

import javax.inject.Singleton;
import java.util.concurrent.CountDownLatch;

/**
 * Modified Account service for concurrency test
 */
@Singleton
public class ConcurrentAccountService extends AccountService {

    /**
     * Test modification of original transfer method: does withdrawing and when waits for another thread to do it's job.
     * After that does the rest of transfer(depositing).
     * @param request   transfer request
     */
    public TransferResult transferWithdrawAndWait(TransferRequest request, CountDownLatch transferLatch,
                                                  CountDownLatch resultLatch) {
        return transaction.executeResult(transactionContext -> {
            try {
                Account sourceAccount = withdraw(transactionContext, request.getSourceAccountId(), request.getAmount());
                transferLatch.countDown(); //signals waiting tread to parallel transfer operation, involving same accounts
                try {
                    Thread.sleep(1000); //waits for another thread to do its job. Not using wait() here to avoid deadlock(since other thread will wait for this transaction to finish)
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Account destinationAccount = deposit(transactionContext, request.getDestinationAccountId(), request.getAmount());

                if (!sourceAccount.getCurrency().equals(destinationAccount.getCurrency())) {
                    throw new IllegalArgumentException(String.format(
                            "Unable to transfer money between accounts with id %d and %d since they have different " +
                                    "currencies (%s and %s respectively).", sourceAccount.getId(), destinationAccount.getId(),
                            sourceAccount.getCurrency().toString(), destinationAccount.getCurrency().toString()));
                }

                return new TransferResult(sourceAccount, destinationAccount);
            } finally {
                resultLatch.countDown(); //Signals main thread that parallel transfer operation is completed
            }
        });
    }

    /**
     * Test modification of original transfer method: waits for another transfer-thread to do its withdraw and then
     * does another complete transfer operation.
     * @param request   transfer request
     */
    public TransferResult transferWaitForWithdraw(TransferRequest request, CountDownLatch transferLatch,
                                                  CountDownLatch resultLatch) {
        try {
            try {
                transferLatch.await(); //Waits for another thread to do withdraw
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return super.transfer(request);
        } finally {
            resultLatch.countDown(); //Signals main thread that parallel transfer operation is completed
        }
    }
}
