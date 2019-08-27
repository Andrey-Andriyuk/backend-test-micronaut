package com.andriyuk.backendtest.accountservice.v0_1.service.transaction;

import org.jooq.DSLContext;

/**
 * Functional interface for executing statements within transaction with returning a result
 */
@FunctionalInterface
public interface TransactionResultCallback<T> {
    T doInTransaction(DSLContext transactionContext);
}
