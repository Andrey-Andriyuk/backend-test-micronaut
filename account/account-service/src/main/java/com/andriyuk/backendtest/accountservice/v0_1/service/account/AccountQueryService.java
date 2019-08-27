package com.andriyuk.backendtest.accountservice.v0_1.service.account;

import com.andriyuk.backendtest.accountservice.v0_1.dao.AccountDao;
import com.andriyuk.backendtest.accountservice.v0_1.service.transaction.TransactionTemplate;
import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigInteger;
import java.util.List;

/**
 * Account query service
 */
@Singleton
public class AccountQueryService {

    //Using custom transaction template implementation, since Spring is prohibited
    @Inject
    protected TransactionTemplate transaction;

    @Inject
    protected AccountDao accountDao;

    /**
     * Returns list of all accounts
     * @return list of account models
     */
    public List<Account> getList() {
        return transaction.executeResult(this::getList);
    }

    /**
     * Requests list of all accounts within specified transaction
     * @param transactionContext    transaction context
     * @return                      list of account models
     */
    protected List<Account> getList(DSLContext transactionContext) {
        return accountDao.getList(transactionContext);
    }

    /**
     * Returns account by specified id
     * @param accountId    account id
     * @return             account model
     */
    public Account getById(BigInteger accountId) {
        return transaction.executeResult(transactionContext -> getById(transactionContext, accountId));
    }

    /**
     * Requests account by specified id within specified transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @return                      account model
     * @throws                      IllegalArgumentException in case of invalid account id
     */
    public Account getById(DSLContext transactionContext, BigInteger id) {
        Account result = accountDao.getById(transactionContext, id);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Missing account with id: %d", id));
        }

        return result;
    }
}
