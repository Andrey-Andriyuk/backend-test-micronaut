package com.andriyuk.backendtest.account.v0_1.dao;

import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.andriyuk.backendtest.api.v0_1.account.AccountState;
import com.andriyuk.backendtest.api.v0_1.account.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.account.Currency;
import com.andriyuk.backendtest.db.jooq.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.andriyuk.backendtest.db.jooq.Tables.ACCOUNT;

/**
 * Data access object for Account table
 */
@Singleton
public class AccountDao {

    /**
     * Returns list of all accounts
     * @return list of account models
     */
    public List<Account> getList(DSLContext transactionContext) {
        return transactionContext.selectFrom(ACCOUNT).fetch().map(AccountDao::createAccountFromRecord);
    }

    /**
     * Returns account by specified id within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @return                      account model
     */
    public Account getById(DSLContext transactionContext, BigInteger id) {
        return transactionContext.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id)).forUpdate().fetchOne(AccountDao::createAccountFromRecord);
    }

    /**
     * Adds a new create account by template within transaction
     * @param transactionContext    transaction context
     * @param accountTemplate       template of account to create
     * @return                      added account model
     */
    public Account add(DSLContext transactionContext, AccountTemplate accountTemplate, AccountState state) {
        return transactionContext.insertInto(ACCOUNT, ACCOUNT.USERID, ACCOUNT.NUMBER, ACCOUNT.BALANCE, ACCOUNT.CURRENCY,
                ACCOUNT.STATE)
                .values(accountTemplate.getUserId(), accountTemplate.getNumber(), accountTemplate.getBalance(),
                        accountTemplate.getCurrency().toString(), state.toString())
                .returning().fetchOne().map(AccountDao::createAccountFromRecord);
    }

    /**
     * Changes balance of specified account within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param newBalance            new balance
     */
    public void changeBalance(DSLContext transactionContext, BigInteger id, BigDecimal newBalance) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(newBalance)).where(ACCOUNT.ID.eq(id)).execute();
    }

    /**
     * Changes state of specified account within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param state                 new account state
     */
    public void changeState(DSLContext transactionContext, BigInteger id, AccountState state) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.STATE, state.toString()).where(ACCOUNT.ID.eq(id)).execute();
    }

    /**
     * Maps Jooq Record class instance on to Account instance
     * @param record    database record
     * @return          account instance
     */
    private static Account createAccountFromRecord(Record record) {
        AccountRecord accountRecord = (AccountRecord) record;
        return new Account(accountRecord.getId(), accountRecord.getUserid(), accountRecord.getNumber(),
                accountRecord.getBalance(), Currency.valueOf(accountRecord.getCurrency()),
                AccountState.valueOf(accountRecord.getState()));
    }
}
