package com.andriyuk.backendtest.account.service;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionTemplate {

    @Inject
    private DSLContext dsl;

    public <T> T execute(TransactionCallback<T> action) {
        return dsl.transactionResult(configuration -> action.doInTransaction(DSL.using(configuration)));
    }
}
