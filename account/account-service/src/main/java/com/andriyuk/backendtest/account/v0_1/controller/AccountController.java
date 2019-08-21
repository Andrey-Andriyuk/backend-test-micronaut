package com.andriyuk.backendtest.account.v0_1.controller;

import com.andriyuk.backendtest.account.v0_1.service.AccountService;

import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.andriyuk.backendtest.api.v0_1.account.AccountOperations;
import com.andriyuk.backendtest.api.v0_1.account.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.transfer.TransferRequest;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;

/**
 * Implementation of account service API
 */
@Validated
public class AccountController implements AccountOperations {

    @Inject
    private AccountService accountService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> getList() {
        return accountService.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getById(@NotBlank BigInteger accountId) {
        return accountService.getById(accountId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account create(@NotBlank AccountTemplate accountTemplate) {
        return accountService.create(accountTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(@NotBlank BigInteger accountId) {
        accountService.close(accountId);
    }

}
