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
import java.util.List;

import static com.andriyuk.backendtest.db.jooq.Tables.ACCOUNT;

@Singleton
public class AccountDao {

    @Inject
    private DSLContext context;

    public AccountDao(DSLContext context) {
        this.context = context;
    }

    public List<Account> getList() {
        return context.selectFrom(ACCOUNT).fetch().map(AccountDao::createAccountFromRecord);
    }

    public Account add(DSLContext transactionContext, AccountTemplate accountTemplate, AccountState state) {
        return transactionContext.insertInto(ACCOUNT, ACCOUNT.USERID, ACCOUNT.NUMBER, ACCOUNT.BALANCE, ACCOUNT.CURRENCY,
                ACCOUNT.STATE)
                .values(accountTemplate.getUserId(), accountTemplate.getNumber(), accountTemplate.getBalance(),
                        accountTemplate.getCurrency().toString(), state.toString())
                .returning().fetchOne().map(AccountDao::createAccountFromRecord);
    }

    private static Account createAccountFromRecord(Record record) {
        AccountRecord accountRecord = (AccountRecord) record;
        return new Account(accountRecord.getId(), accountRecord.getUserid(), accountRecord.getNumber(),
                accountRecord.getBalance(), Currency.valueOf(accountRecord.getCurrency()),
                AccountState.valueOf(accountRecord.getState()));
    }
}
