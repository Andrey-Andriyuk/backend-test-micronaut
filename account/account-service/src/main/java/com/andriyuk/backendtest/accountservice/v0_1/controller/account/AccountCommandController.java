package com.andriyuk.backendtest.accountservice.v0_1.controller.account;

import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.AccountCommandOperations;
import com.andriyuk.backendtest.accountapi.v0_1.account.AccountTemplate;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountCommandService;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * Implementation of account query API
 */
@Validated
public class AccountCommandController implements AccountCommandOperations {

    @Inject
    private AccountCommandService accountCommandService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Account create(@NotBlank AccountTemplate accountTemplate) {
        return accountCommandService.create(accountTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(@NotBlank BigInteger accountId) {
        accountCommandService.close(accountId);
    }
}
