package com.andriyuk.backendtest.account.service;

import com.andriyuk.backendtest.account.dao.AccountDao;
import com.andriyuk.backendtest.api.v0_1.*;
import org.jooq.DSLContext;

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
        return transaction.executeResult(transactionContext -> getList(transactionContext));
    }

    //todo JavaDoc
    protected List<Account> getList(DSLContext transactionContext) {
        return accountDao.getList(transactionContext);
    }

    //todo JavaDoc
    public Account getById(BigInteger id) {
        return transaction.executeResult(transactionContext -> getById(transactionContext, id));
    }

    //todo JavaDoc
    protected Account getById(DSLContext transactionContext, BigInteger id) {
        Account result = accountDao.getById(transactionContext, id);
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

        checkAmountNonNegative(accountTemplate.getBalance());
    }

    //todo JavaDoc
    //todo Возвращать измененный объект
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

    //todo JavaDoc
    public Account deposit(BigInteger id, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> deposit(transactionContext, id, amount));
    }

    //todo JavaDoc
    public Account deposit(DSLContext transactionContext, BigInteger id, BigDecimal amount) {
        Account account = getById(transactionContext, id);
        checkBalanceOperation(account, amount);
        return accountDao.changeBalance(transactionContext, id, amount);
    }

    //todo JavaDoc
    public Account withdraw(BigInteger id, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> withdraw(transactionContext, id, amount));
    }

    //todo JavaDoc
    protected Account withdraw(DSLContext transactionContext, BigInteger id, BigDecimal amount) {
        Account account = getById(transactionContext, id);
        checkBalanceOperation(account, amount);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(String.format(
                    "Withdrawing amount (%f) exceeds account balance. " +
                            "Unable to perform operation on account with id %d.", amount, id));
        }

        return accountDao.changeBalance(transactionContext, id, amount.negate());
    }

    //todo JavaDoc
    public TransferResult transfer(TransferRequest request) {//BigInteger sourceAccountId, BigInteger destinationAccountId, BigDecimal amount) {
        return transaction.executeResult(transactionContext -> {
            Account sourceAccount = withdraw(transactionContext, request.getSourceAccountId(), request.getAmount());
            Account destinationAccount = deposit(transactionContext, request.getDestinationAccountId(), request.getAmount());

            if (!sourceAccount.getCurrency().equals(destinationAccount.getCurrency())) {
                throw new IllegalArgumentException(String.format(
                        "Unable to transfer money between accounts with id %d and %d since they have different " +
                                "currencies (%s and %s respectively).", sourceAccount.getId(), destinationAccount.getId(),
                        sourceAccount.getCurrency().toString(), destinationAccount.getCurrency().toString()));
            }

            return new TransferResult(sourceAccount, destinationAccount);
        });
    }

    //todo JavaDoc
    public void checkBalanceOperation(Account account, BigDecimal amount) {
        if (account.getState() == AccountState.CLOSED) {
            throw new IllegalStateException(String.format(
                    "Unable to perform balance operation on closed account with id: %d", account.getId()));
        }

        checkAmountPositive(amount);
    }

    //todo JavaDoc
    protected void checkAmountPositive(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unable to perform balance operation with non positive amount");
        }
    }

    //todo JavaDoc
    protected void checkAmountNonNegative(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unable to perform balance operation with negative amount");
        }
    }

}
