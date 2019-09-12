package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.account.v0_1.dao.AccountDao;
import com.andriyuk.backendtest.account.v0_1.service.transaction.TransactionTemplate;
import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.andriyuk.backendtest.api.v0_1.account.AccountState;
import com.andriyuk.backendtest.api.v0_1.account.AccountTemplate;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Account service
 */
@Singleton
public class AccountService {

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
        return transaction.executeResult(transactionContext -> getList(transactionContext));
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
    protected Account getById(DSLContext transactionContext, BigInteger id) {
        Account result = accountDao.getById(transactionContext, id);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Missing account with id: %d", id));
        }

        return result;
    }

    /**
     * Creates a new create account by template
     * @param accountTemplate   template of account to create
     * @return                  added account model
     */
    public Account create(AccountTemplate accountTemplate) {
        return transaction.executeResult(transactionContext -> {
            checkAccountTemplate(accountTemplate);
            return accountDao.add(transactionContext, accountTemplate, AccountState.OPENED);
        });
    }

    /**
     * Perform basic validation of account template
     * @param accountTemplate   account template
     * @throws                  IllegalArgumentException in case of invalid account number
     */
    public void checkAccountTemplate(AccountTemplate accountTemplate) {
        if (accountTemplate.getNumber().length() > Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH) {
            throw new IllegalArgumentException(String.format("Account number length(%d) doesn't fit IBAN restrictions",
                    accountTemplate.getNumber().length()));
        }

        checkAmountNonNegative(accountTemplate.getBalance());
    }

    /**
     * Close specified account
     * @param accountId             account id
     * @throws                 IllegalStateException in case of invalid account state or if account is already changed
     */
    public void close(BigInteger accountId) throws IllegalStateException {
        transaction.execute(transactionContext -> {
            Account actualAccount = getById(accountId);
            if (actualAccount.getState() == AccountState.CLOSED) {
                throw new IllegalStateException(String.format("Account with id %d is already closed.", actualAccount.getId()));
            } else if (!actualAccount.getBalance().equals(BigDecimal.ZERO)) {
                throw new IllegalStateException(String.format("Closing non empty account with id %d is prohibited.", actualAccount.getId()));
            }

            accountDao.changeState(transactionContext, actualAccount.getId(), AccountState.CLOSED);
        });
    }

    /**
     * Checks if account is in OPENED state. Checks is specified amount is valid
     * @param account       account model
     * @param amount        money amount
     * @throws              IllegalArgumentException in case of check failure
     */
    public void checkBalanceState(Account account, BigDecimal amount) {
        if (account.getState() == AccountState.CLOSED) {
            throw new IllegalStateException(String.format(
                    "Unable to perform balance operation on closed account with id: %d", account.getId()));
        }

        checkAmountPositive(amount);
    }

    /**
     * Checks is specified amount is positive
     * @param amount    money amount
     * @throws          IllegalArgumentException in case of check failure
     */
    protected void checkAmountPositive(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unable to perform balance operation with non positive amount");
        }
    }

    /**
     * Checks is specified amount is not negative
     * @param amount    money amount
     * @throws          IllegalArgumentException in case of check failure
     */
    protected void checkAmountNonNegative(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unable to perform balance operation with negative amount");
        }
    }

    /**
     * Checks that account, involved in some operation, is the same as stored in DB.
     * @param accountDescription        description of account involved in some operation
     * @param actualAccount             actual account
     * @throws IllegalStateException    in case of non equal accounts
     */
    protected void checkAccountChanged(AccountTemplate accountDescription, Account actualAccount) {
        if (!accountDescription.equals(actualAccount)) {
            throw new IllegalStateException(
                    String.format("Account with id \"%d\" has been changed elsewhere. Unable to perform requested operation.",
                            actualAccount.getId()));
        }
    }

}
