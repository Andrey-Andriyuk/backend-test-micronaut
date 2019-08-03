package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.account.dao.AccountDao;
import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountState;
import com.andriyuk.backendtest.api.v0_1.AccountTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

//todo JavaDoc
@Singleton
public class AccountService {

    @Inject
    private TransactionTemplate transaction;

    @Inject
    private AccountDao accountDao;

    //todo JavaDoc
    public List<Account> getList() {
        return accountDao.getList();
    }

    //todo JavaDoc
    public Account getById(BigInteger id) {
        Account result = accountDao.getById(id);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Missing account with id: %d", id));
        }

        return result;
    }

    //todo JavaDoc
    public Account add(AccountTemplate accountTemplate) {
        return transaction.executeResult(transactionContext -> {
            checkAccountTemplate(accountTemplate);
            return accountDao.add(transactionContext, accountTemplate, AccountState.OPENED);
        });
    }

    //todo JavaDoc
    public void checkAccountTemplate(AccountTemplate accountTemplate) {
        if (accountTemplate.getNumber().length() > Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException(String.format("Account number length(%d) doesn't fit IBAN restrictions",
                    accountTemplate.getNumber().length()));
        }
    }

    //todo JavaDoc
    //todo Возвращать измененный объект
    public void close(BigInteger id) {
        transaction.execute(transactionContext -> {
            Account account = getById(id);
            if (account.getState() == AccountState.CLOSED) {
                throw new IllegalStateException(String.format("Account with id %d is already closed.", id));
            } else if (!account.getBalance().equals(BigDecimal.ZERO)) {
                throw new IllegalStateException(String.format("Closing non empty account with id %d is prohibited.", id));
            }

            accountDao.changeState(transactionContext, id, AccountState.CLOSED);
        });
    }
}
