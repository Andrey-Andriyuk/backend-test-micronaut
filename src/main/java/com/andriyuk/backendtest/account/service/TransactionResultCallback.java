package com.andriyuk.backendtest.account.service;

import org.jooq.DSLContext;

@FunctionalInterface
public interface TransactionResultCallback<T> {
    T doInTransaction(DSLContext transactionContext);
}
