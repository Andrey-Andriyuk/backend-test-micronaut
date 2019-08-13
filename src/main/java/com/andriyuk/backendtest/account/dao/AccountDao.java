package com.andriyuk.backendtest.account.dao;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountState;
import com.andriyuk.backendtest.api.v0_1.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.CurrencyCode;
import com.andriyuk.backendtest.currency.dao.CurrencyDao;
import com.andriyuk.backendtest.db.jooq.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.inject.Inject;
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

    @Inject
    CurrencyDao currencyDao;

    /**
     * Returns list of all accounts
     * @return list of account models
     */
    public List<Account> getList(DSLContext transactionContext) {
        return transactionContext.selectFrom(ACCOUNT).fetch().map(record -> createAccountFromRecord(record));
    }

    /**
     * Returns account by specified id within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @return                      account model
     */
    public Account getById(DSLContext transactionContext, BigInteger id) {
        return transactionContext.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id)).fetchOne(record -> createAccountFromRecord(record));
    }

    /**
     * Adds a new open account by template within transaction
     * @param transactionContext    transaction context
     * @param accountTemplate       template of account to add
     * @return                      added account model
     */
    public Account add(DSLContext transactionContext, AccountTemplate accountTemplate, AccountState state) {
        //Getting currencyId out of transaction bounds since it's value cached anyway.
        //Keep in mind that existing records in Currency table are immutable.
        BigInteger currencyId = currencyDao.getIdByCode(accountTemplate.getCurrencyCode());

        return transactionContext.insertInto(ACCOUNT, ACCOUNT.USER_ID, ACCOUNT.NUMBER, ACCOUNT.BALANCE, ACCOUNT.CURRENCY_ID,
                ACCOUNT.STATE)
                .values(accountTemplate.getUserId(), accountTemplate.getNumber(), accountTemplate.getBalance(), currencyId,
                        state.toString())
                .returning().fetchOne().map(record -> createAccountFromRecord(record));
    }

    /**
     * Changes balance of specified account within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param newBalance            new balance
     * @return                      model of modified account
     */
    public Account changeBalance(DSLContext transactionContext, BigInteger id, BigDecimal newBalance) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(newBalance)).where(ACCOUNT.ID.eq(id)).execute();
        // Since H2 does not support UPDATE ... RETURNING statement, a modified account is requested here (in the context of a transaction)
        return getById(transactionContext, id);
    }

    /**
     * Changes state of specified account within transaction
     * @param transactionContext    transaction context
     * @param id                    account id
     * @param state                 new account state
     * @return                      model of modified account
     */
    public Account changeState(DSLContext transactionContext, BigInteger id, AccountState state) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.STATE, state.toString()).where(ACCOUNT.ID.eq(id)).execute();
        // Since H2 does not support UPDATE ... RETURNING statement, a modified account is requested here (in the context of a transaction)
        return getById(transactionContext, id);
    }

    /**
     * Maps Jooq Record class instance on to Account instance
     * @param record    database record
     * @return          account instance
     */
    private Account createAccountFromRecord(Record record) {
        AccountRecord accountRecord = (AccountRecord) record;

        //Getting currency code out of transaction bounds since it's value cached anyway.
        //Keep in mind that existing records in Currency table are immutable.
        CurrencyCode currencyCode = currencyDao.getCodeById(accountRecord.getCurrencyId());

        return new Account(accountRecord.getId(), accountRecord.getUserId(), accountRecord.getNumber(),
                accountRecord.getBalance(), currencyCode, AccountState.valueOf(accountRecord.getState()));
    }
}
