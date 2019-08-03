package com.andriyuk.backendtest.account.dao;

import com.andriyuk.backendtest.api.v0_1.Account;
import com.andriyuk.backendtest.api.v0_1.AccountState;
import com.andriyuk.backendtest.api.v0_1.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.Currency;
import com.andriyuk.backendtest.db.jooq.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.andriyuk.backendtest.db.jooq.Tables.ACCOUNT;

//todo JavaDoc
@Singleton
public class AccountDao {

    //todo JavaDoc
    public List<Account> getList(DSLContext transactionContext) {
        return transactionContext.selectFrom(ACCOUNT).fetch().map(AccountDao::createAccountFromRecord);
    }

    //todo JavaDoc
    public Account getById(DSLContext transactionContext, BigInteger id) {
        return transactionContext.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(id)).fetchOne(AccountDao::createAccountFromRecord);
    }

    //todo JavaDoc
    public Account add(DSLContext transactionContext, AccountTemplate accountTemplate, AccountState state) {
        return transactionContext.insertInto(ACCOUNT, ACCOUNT.USERID, ACCOUNT.NUMBER, ACCOUNT.BALANCE, ACCOUNT.CURRENCY,
                ACCOUNT.STATE)
                .values(accountTemplate.getUserId(), accountTemplate.getNumber(), accountTemplate.getBalance(),
                        accountTemplate.getCurrency().toString(), state.toString())
                .returning().fetchOne().map(AccountDao::createAccountFromRecord);
    }

    //todo JavaDoc
    public Account changeBalance(DSLContext transactionContext, BigInteger id, BigDecimal amount) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(amount)).where(ACCOUNT.ID.eq(id)).execute();
        // Since H2 does not support the UPDATE ... RETURNING statement, a modified account is requested here (in the context of a transaction)
        return getById(transactionContext, id);
    }

    //todo JavaDoc
    public Account changeState(DSLContext transactionContext, BigInteger id, AccountState state) {
        transactionContext.update(ACCOUNT).set(ACCOUNT.STATE, state.toString()).where(ACCOUNT.ID.eq(id)).execute();
        // Since H2 does not support the UPDATE ... RETURNING statement, a modified account is requested here (in the context of a transaction)
        return getById(transactionContext, id);
    }

    //todo JavaDoc
    private static Account createAccountFromRecord(Record record) {
        AccountRecord accountRecord = (AccountRecord) record;
        return new Account(accountRecord.getId(), accountRecord.getUserid(), accountRecord.getNumber(),
                accountRecord.getBalance(), Currency.valueOf(accountRecord.getCurrency()),
                AccountState.valueOf(accountRecord.getState()));
    }
}
