package com.andriyuk.backendtest.account.v0_1.service.transaction;

import org.jooq.DSLContext;

/**
 * Functional interface for executing statements within transaction without returning a result
 */
@FunctionalInterface
public interface TransactionCallback {
    void doInTransaction(DSLContext transactionContext) throws InterruptedException;
}
