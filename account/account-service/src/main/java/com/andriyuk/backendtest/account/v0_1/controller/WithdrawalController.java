package com.andriyuk.backendtest.account.v0_1.controller;

import com.andriyuk.backendtest.account.v0_1.service.WithdrawalService;
import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.api.v0_1.WithdrawalOperations;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Implementation of withdrawal service API
 */
@Validated
public class WithdrawalController implements WithdrawalOperations {

    @Inject
    private WithdrawalService withdrawalService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(@NotBlank BigInteger accountId, @NotBlank BalanceChangeRequest balanceChangeRequest) {
        withdrawalService.create(accountId, balanceChangeRequest);
    }
}
