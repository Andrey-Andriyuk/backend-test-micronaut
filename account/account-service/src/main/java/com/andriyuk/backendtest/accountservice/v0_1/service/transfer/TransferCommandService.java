package com.andriyuk.backendtest.accountservice.v0_1.service.transfer;

import com.andriyuk.backendtest.accountservice.v0_1.service.deposit.DepositCommandService;
import com.andriyuk.backendtest.accountservice.v0_1.service.transaction.TransactionTemplate;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.accountapi.v0_1.transfer.TransferRequest;
import com.andriyuk.backendtest.accountservice.v0_1.service.withdrawal.WithdrawalCommandService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transfer service
 */
@Singleton
public class TransferCommandService {

    //Using custom transaction template implementation, since Spring is prohibited
    @Inject
    protected TransactionTemplate transaction;

    @Inject
    protected WithdrawalCommandService withdrawalCommandService;

    @Inject
    protected DepositCommandService depositCommandService;

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

            withdrawalCommandService.create(transactionContext, request.getSourceAccount().getId(),
                    new BalanceChangeRequest(request.getSourceAccount(), request.getAmount()));

            depositCommandService.create(transactionContext, request.getDestinationAccount().getId(),
                    new BalanceChangeRequest(request.getDestinationAccount(),request.getAmount()));
        });
    }

}
