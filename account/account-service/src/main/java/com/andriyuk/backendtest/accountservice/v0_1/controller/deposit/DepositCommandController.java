package com.andriyuk.backendtest.accountservice.v0_1.controller.deposit;

import com.andriyuk.backendtest.accountservice.v0_1.service.deposit.DepositCommandService;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import com.andriyuk.backendtest.accountapi.v0_1.deposit.DepositCommandOperations;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Implementation of deposit service API
 */
@Validated
public class DepositCommandController implements DepositCommandOperations {

    @Inject
    private DepositCommandService depositCommandService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(@NotBlank BigInteger accountId, @NotBlank BalanceChangeRequest balanceChangeRequest) {
        depositCommandService.create(accountId, balanceChangeRequest);
    }

}
