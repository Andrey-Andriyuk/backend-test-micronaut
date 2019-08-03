package com.andriyuk.backendtest.account.service;

import org.jooq.DSLContext;

@FunctionalInterface
public interface TransactionCallback {
    void doInTransaction(DSLContext transactionContext);
}
