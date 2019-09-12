package com.andriyuk.backendtest.account.v0_1.controller;

import com.andriyuk.backendtest.account.v0_1.service.TransferService;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferOperations;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferRequest;

import javax.inject.Inject;

/**
 * Implementation of transfer service API
 */
public class TransferController implements TransferOperations {

    @Inject
    private TransferService transferService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TransferRequest transferRequest) {
        transferService.create(transferRequest);
    }

}
