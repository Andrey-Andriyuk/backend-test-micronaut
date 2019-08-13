package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.account.dao.AccountDao;
import com.andriyuk.backendtest.api.v0_1.*;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Domain service of accounts
 */
@Singleton
public class AccountService {

    //Using custom transaction template implementation, since Spring is prohibited
    @Inject
    private TransactionTemplate transaction;

    @Inject
    private AccountDao accountDao;

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
     * @param id    account id
     * @return      account model
     */
    public Account getById(BigInteger id) {
        return transaction.executeResult(transactionContext -> getById(transactionContext, id));
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
     * Adds a new open account by template
     * @param accountTemplate   template of account to add
     * @return                  added account model
     */
    public Account add(AccountTemplate accountTemplate) {
        //todo запрещать добавлять счета с номерами из системного списка(SYSTEM-COMMISSION, ...)
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
     * Close account by specified id
     * @param id        account id
     * @return          closed account model
     * @throws          IllegalStateException in case of invalid account state
     */
    public Account close(BigInteger id) {
        return transaction.executeResult(transactionContext -> {
            Account account = getById(id);
            if (account.getState() == AccountState.CLOSED) {
                throw new IllegalStateException(String.format("Account with id %d is already closed.", id));
            } else if (!account.getBalance().equals(BigDecimal.ZERO)) {
                throw new IllegalStateException(String.format("Closing non empty account with id %d is prohibited.", id));
            }

            return accountDao.changeState(transactionContext, id, AccountState.CLOSED);
        });
    }

    /**
     * Deposits specified amount of money from specified account
     * @param id        account id
     * @param amount    amount to deposit
     * @return          model of modified account
     */
    public Account deposit(BigInteger id, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> deposit(transactionContext, id, amount));
    }

    /**
     * Deposits specified amount of money from specified account within specified transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param amount                amount to deposit
     * @return                      model of modified account
     */
    protected Account deposit(DSLContext transactionContext, BigInteger id, BigDecimal amount) {
        Account account = getById(transactionContext, id);
        checkBalanceState(account, amount);
        return accountDao.changeBalance(transactionContext, id, amount);
    }

    /**
     * Withdraws specified amount of money from specified account
     * @param id        account id
     * @param amount    amount to withdraw
     * @return          model of modified account
     */
    public Account withdraw(BigInteger id, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> withdraw(transactionContext, id, amount));
    }

    /**
     * Withdraws specified amount of money from specified account within specified transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param amount                amount to withdraw
     * @return                      model of modified account
     * @throws                      IllegalArgumentException in case of insufficient account balance
     */
    protected Account withdraw(DSLContext transactionContext, BigInteger id, BigDecimal amount) {
        Account account = getById(transactionContext, id);
        checkBalanceState(account, amount);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(String.format(
                    "Withdrawing amount (%f) exceeds account balance. " +
                            "Unable to perform operation on account with id %d.", amount, id));
        }

        return accountDao.changeBalance(transactionContext, id, amount.negate());
    }

    /**
     * Transfer specified amount of money from one account to another
     * @param request           request model for money transfer
     * @return                  result model of money transfer
     * @throws                  IllegalArgumentException in case of different accounts currencies
     */
    public TransferResult transfer(TransferRequest request) {//BigInteger sourceAccountId, BigInteger destinationAccountId, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> {
            Account sourceAccount = withdraw(transactionContext, request.getSourceAccountId(), request.getAmount());
            Account destinationAccount = deposit(transactionContext, request.getDestinationAccountId(), request.getAmount());

            if (!sourceAccount.getCurrencyCode().equals(destinationAccount.getCurrencyCode())) {
                throw new IllegalArgumentException(String.format(
                        "Unable to transfer money between accounts with id %d and %d since they have different " +
                                "currencies (%s and %s respectively).", sourceAccount.getId(), destinationAccount.getId(),
                        sourceAccount.getCurrencyCode().toString(), destinationAccount.getCurrencyCode().toString()));
            }

            return new TransferResult(sourceAccount, destinationAccount);
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

}
