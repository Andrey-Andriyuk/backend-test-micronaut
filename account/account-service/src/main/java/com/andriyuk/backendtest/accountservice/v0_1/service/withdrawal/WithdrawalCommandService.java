package com.andriyuk.backendtest.accountservice.v0_1.service.withdrawal;

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
 * Withdrawal command service
 */
@Singleton
public class WithdrawalCommandService {

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
     * Create withdrawal operation for account
     * @param accountId       account id
     * @param changeRequest   create operation model
     */
    public void create(BigInteger accountId, BalanceChangeRequest changeRequest) {
        transaction.execute(transactionContext -> create(transactionContext, accountId, changeRequest));
    }

    /**
     * Create withdrawal operation for account within specified transaction
     * @param transactionContext    transaction context
     * @param accountId             account Id
     * @param changeRequest         create request model
     * @throws                      IllegalArgumentException in case of insufficient account balance
     */
    public void create(DSLContext transactionContext, BigInteger accountId, BalanceChangeRequest changeRequest) {
        Account actualAccount = accountQueryService.getById(transactionContext, accountId);

        accountCommandService.checkAccountChanged(changeRequest.getAccountDescription(), actualAccount);
        accountCommandService.checkBalanceState(actualAccount, changeRequest.getAmount());

        if (actualAccount.getBalance().compareTo(changeRequest.getAmount()) < 0) {
            throw new IllegalArgumentException(String.format(
                    "Withdrawing amount (%f) exceeds account balance. " +
                            "Unable to perform operation on account with id %d.", changeRequest.getAmount(),
                    actualAccount.getId()));
        }

        accountDao.changeBalance(transactionContext, actualAccount.getId(), changeRequest.getAmount().negate());
    }

    //todo methods for querying withdrawals by filter(user query params)
}
