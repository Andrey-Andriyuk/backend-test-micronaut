package com.andriyuk.backendtest.accountservice.v0_1.service.deposit;

import com.andriyuk.backendtest.accountservice.v0_1.dao.AccountDao;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountCommandService;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountQueryService;
import com.andriyuk.backendtest.accountservice.v0_1.service.transaction.TransactionTemplate;
import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigInteger;

/**
 * Deposit service
 */
@Singleton
public class DepositCommandService {

    //Using custom transaction template implementation, since Spring is prohibited
    @Inject
    protected TransactionTemplate transaction;

    @Inject
    protected AccountQueryService accountQueryService;
    @Inject
    protected AccountCommandService accountCommandService;


    @Inject
    protected AccountDao accountDao;

    /**
     * Creates deposit for specified account and amount
     * @param accountId       account id
     * @param changeRequest   create operation model
     */
    public void create(BigInteger accountId, BalanceChangeRequest changeRequest) {
        transaction.execute(transactionContext -> create(transactionContext, accountId, changeRequest));
    }

    /**
     * Creates deposit for specified account and amount within specified transaction
     * @param transactionContext    transaction context
     * @param accountId             account id
     * @param changeRequest         create operation model
     */
    public void create(DSLContext transactionContext, BigInteger accountId, BalanceChangeRequest changeRequest) {
        Account actualAccount = accountQueryService.getById(transactionContext, accountId);

        accountCommandService.checkAccountChanged(changeRequest.getAccountDescription(), actualAccount);
        accountCommandService.checkBalanceState(actualAccount, changeRequest.getAmount());

        accountDao.changeBalance(transactionContext, actualAccount.getId(), changeRequest.getAmount());
    }
}
