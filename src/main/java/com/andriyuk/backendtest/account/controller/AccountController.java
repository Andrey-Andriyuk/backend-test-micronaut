package com.andriyuk.backendtest.account.controller;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountOperations;
import com.andriyuk.backendtest.account.service.AccountService;

import com.andriyuk.backendtest.api.v0_1.AccountTemplate;
import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Validated
@Controller("/")
public class AccountController implements AccountOperations {

    @Inject
    private AccountService accountService;

    @Override
    public List<Account> getList() {
        return accountService.getList();
    }

    @Override
    public Account getById(@NotBlank BigInteger id) {
        return accountService.getById(id);
    }

    @Override
    public Account add(@NotBlank AccountTemplate accountTemplate) {
        return accountService.add(accountTemplate);
    }

    @Override
    public void close(@NotBlank BigInteger id) {
        accountService.close(id);
    }

    @Override
    public Account withdraw(BigInteger id, BigDecimal amount) {
        return accountService.withdraw(id, amount);
    }
}
