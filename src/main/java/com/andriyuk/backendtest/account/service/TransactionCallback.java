package com.andriyuk.backendtest.account.service;

import org.jooq.DSLContext;

@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(DSLContext transactionContext);
}
