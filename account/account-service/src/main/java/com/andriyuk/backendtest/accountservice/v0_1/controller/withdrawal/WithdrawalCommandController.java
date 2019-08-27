package com.andriyuk.backendtest.accountservice.v0_1.controller.withdrawal;

import com.andriyuk.backendtest.accountservice.v0_1.service.withdrawal.WithdrawalCommandService;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.accountapi.v0_1.withdrawal.WithdrawalCommandOperations;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Implementation of withdrawal service API
 */
@Validated
public class WithdrawalCommandController implements WithdrawalCommandOperations {

    @Inject
    private WithdrawalCommandService withdrawalCommandService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(@NotBlank BigInteger accountId, @NotBlank BalanceChangeRequest balanceChangeRequest) {
        withdrawalCommandService.create(accountId, balanceChangeRequest);
    }
}
