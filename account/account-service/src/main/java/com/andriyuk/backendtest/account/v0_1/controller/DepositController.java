package com.andriyuk.backendtest.account.v0_1.controller;

import com.andriyuk.backendtest.account.v0_1.service.DepositService;
import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.api.v0_1.DepositOperations;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Implementation of deposit service API
 */
@Validated
public class DepositController implements DepositOperations {

    @Inject
    private DepositService depositService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(@NotBlank BigInteger accountId, @NotBlank BalanceChangeRequest balanceChangeRequest) {
        depositService.create(accountId, balanceChangeRequest);
    }

}
