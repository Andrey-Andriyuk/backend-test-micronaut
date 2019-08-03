package com.andriyuk.backendtest.account.controller;

import com.andriyuk.backendtest.api.v0_1.*;
import com.andriyuk.backendtest.account.service.AccountService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Implementation of account service API
 */
@Validated
@Controller("/")
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
    public Account getById(@NotBlank BigInteger id) {
        return accountService.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account add(@NotBlank AccountTemplate accountTemplate) {
        return accountService.add(accountTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account close(@NotBlank BigInteger id) {
        return accountService.close(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account withdraw(BigInteger id, BigDecimal amount) {
        return accountService.withdraw(id, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account deposit(BigInteger id, BigDecimal amount) {
        return accountService.deposit(id, amount);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TransferResult transfer(TransferRequest transferRequest) {
        return accountService.transfer(transferRequest);
    }
}
