package com.andriyuk.backendtest.accountservice.v0_1.controller.account;

import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountQueryService;

import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.AccountQueryOperations;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;

/**
 * Implementation of account query API
 */
@Validated
public class AccountQueryController implements AccountQueryOperations {

    @Inject
    private AccountQueryService accountQueryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> getList() {
        return accountQueryService.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getById(@NotBlank BigInteger accountId) {
        return accountQueryService.getById(accountId);
    }
}
