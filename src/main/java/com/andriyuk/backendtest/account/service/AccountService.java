package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.account.dao.AccountDao;
import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountState;
import com.andriyuk.backendtest.api.v0_1.AccountTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AccountService {

    @Inject
    private TransactionTemplate transaction;

    @Inject
    private AccountDao accountDao;

    public List<Account> getList() {
        return accountDao.getList();
    }

    public Account add(AccountTemplate accountTemplate) {
        return transaction.execute(transactionContext -> accountDao.add(transactionContext, accountTemplate, AccountState.OPENED));
    }
}
