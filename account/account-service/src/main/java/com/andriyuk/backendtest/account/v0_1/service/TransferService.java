package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.account.v0_1.service.transaction.TransactionTemplate;
import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transfer service
 */
@Singleton
public class TransferService {

    //Using custom transaction template implementation, since Spring is prohibited
    @Inject
    protected TransactionTemplate transaction;

    @Inject
    protected WithdrawalService withdrawalService;

    @Inject
    protected DepositService depositService;

    /**
     * Creates create from one account to another
     * @param request           create request model
     * @throws                  IllegalArgumentException in case of different accounts currencies
     */
    public void create(TransferRequest request) {
        transaction.execute(transactionContext -> {
            if (!request.getSourceAccount().getCurrency().equals(request.getDestinationAccount().getCurrency())) {
                throw new IllegalArgumentException(String.format(
                        "Unable to create money between accounts since they have different " +
                                "currencies (%s and %s respectively).", request.getSourceAccount().getCurrency().toString(),
                        request.getDestinationAccount().getCurrency().toString()));
            }

            withdrawalService.create(transactionContext, request.getSourceAccount().getId(),
                    new BalanceChangeRequest(request.getSourceAccount(), request.getAmount()));

            depositService.create(transactionContext, request.getDestinationAccount().getId(),
                    new BalanceChangeRequest(request.getDestinationAccount(),request.getAmount()));
        });
    }

}
