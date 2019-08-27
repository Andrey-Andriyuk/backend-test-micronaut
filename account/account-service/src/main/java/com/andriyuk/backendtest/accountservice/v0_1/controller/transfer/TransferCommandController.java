package com.andriyuk.backendtest.accountservice.v0_1.controller.transfer;

import com.andriyuk.backendtest.accountservice.v0_1.service.transfer.TransferCommandService;
import com.andriyuk.backendtest.accountapi.v0_1.transfer.TransferCommandOperations;
import com.andriyuk.backendtest.accountapi.v0_1.transfer.TransferRequest;

import javax.inject.Inject;

/**
 * Implementation of transfer service API
 */
public class TransferCommandController implements TransferCommandOperations {

    @Inject
    private TransferCommandService transferCommandService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TransferRequest transferRequest) {
        transferCommandService.create(transferRequest);
    }

}
