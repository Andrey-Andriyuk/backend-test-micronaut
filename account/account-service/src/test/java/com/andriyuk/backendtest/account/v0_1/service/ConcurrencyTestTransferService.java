package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferRequest;

import javax.inject.Singleton;
import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;

/**
 * Modified Account service for concurrency test
 */
@Singleton
public class ConcurrencyTestTransferService extends TransferService {

    /**
     * Test modification of original create method: does withdrawing and when waits for another thread to do complete create operation
     * After that does the rest of create(depositing).
     * @param request   create request
     */
    public void withdrawWaitAndDeposit(TransferRequest request, CountDownLatch transferLatch,
                                                 CountDownLatch resultLatch) {
        transaction.execute(transactionContext -> {
            try {
                if (!request.getSourceAccount().getCurrency().equals(request.getDestinationAccount().getCurrency())) {
                    throw new IllegalArgumentException(String.format(
                            "Unable to create money between accounts since they have different " +
                                    "currencies (%s and %s respectively).", request.getSourceAccount().getCurrency().toString(),
                            request.getDestinationAccount().getCurrency().toString()));
                }

                withdrawalService.create(transactionContext, request.getSourceAccount().getId(),
                        new BalanceChangeRequest(request.getSourceAccount(), request.getAmount()));

                transferLatch.countDown(); //signals waiting tread to parallel create operation, involving same accounts
                try {
                    Thread.sleep(1000); //waits for another thread to do its job. Not using wait() here to avoid deadlock(since other thread will wait for this transaction to finish)
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                depositService.create(transactionContext, request.getDestinationAccount().getId(),
                        new BalanceChangeRequest(request.getDestinationAccount(),request.getAmount()));

            } finally {
                resultLatch.countDown(); //Signals main thread that parallel create operation is completed
            }
        });
    }

    /**
     * Test modification of original create method: waits for another create-thread to do its create and then
     * does another complete create operation.
     * @param request   create request
     */
    public void waitForWithdrawAndTransfer(TransferRequest request, CountDownLatch transferLatch,
                                                     CountDownLatch resultLatch) {
        try {
            try {
                transferLatch.await(); //Waits for another thread to do create
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            super.create(request);
        } finally {
            resultLatch.countDown(); //Signals main thread that parallel create operation is completed
        }
    }

    public void withdrawAndWait(TransferRequest request, CountDownLatch withdrawLatch) {
        transaction.execute(transactionContext -> {
            if (!request.getSourceAccount().getCurrency().equals(request.getDestinationAccount().getCurrency())) {
                throw new IllegalArgumentException(String.format(
                        "Unable to create money between accounts since they have different " +
                                "currencies (%s and %s respectively).", request.getSourceAccount().getCurrency().toString(),
                        request.getDestinationAccount().getCurrency().toString()));
            }

            BigInteger destAccountId = request.getDestinationAccount().getId();
            BigInteger sourcAccountId = request.getSourceAccount().getId();

            if (destAccountId.compareTo(sourcAccountId) < 0) {
                withdrawalService.create(transactionContext, request.getSourceAccount().getId(),
                        new BalanceChangeRequest(request.getSourceAccount(), request.getAmount()));
            } else {
                depositService.create(transactionContext, request.getDestinationAccount().getId(),
                        new BalanceChangeRequest(request.getDestinationAccount(),request.getAmount()));
            }


//            withdrawLatch.countDown();
//            withdrawLatch.await();

            if (destAccountId.compareTo(sourcAccountId) < 0) {
                depositService.create(transactionContext, request.getDestinationAccount().getId(),
                        new BalanceChangeRequest(request.getDestinationAccount(),request.getAmount()));
            } else {
                withdrawalService.create(transactionContext, request.getSourceAccount().getId(),
                        new BalanceChangeRequest(request.getSourceAccount(), request.getAmount()));
            }
        });
    }
}
