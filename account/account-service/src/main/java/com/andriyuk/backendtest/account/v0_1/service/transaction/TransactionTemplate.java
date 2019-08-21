package com.andriyuk.backendtest.account.v0_1.service.transaction;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transaction template for executing operations withing transaction
 */
@Singleton
public class TransactionTemplate {

    @Inject
    private DSLContext dsl;

    public <T> T executeResult(TransactionResultCallback<T> action) {
        return dsl.transactionResult(configuration -> action.doInTransaction(DSL.using(configuration)));
    }

    public <T> void execute(TransactionCallback action) {
        dsl.transaction(configuration -> action.doInTransaction(DSL.using(configuration)));
    }
}
