package com.andriyuk.backendtest.currency.dao;

import com.andriyuk.backendtest.api.v0_1.CurrencyCode;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigInteger;

import static com.andriyuk.backendtest.db.jooq.Tables.CURRENCY;

//todo This should be a separate microservice, which works with currencies
//todo JavaDoc
@Singleton
public class CurrencyDao {

    @Inject
    private DSLContext dsl;


    //todo Apply caching on this DAO methods
    //todo JavaDoc
    public BigInteger getIdByCode(CurrencyCode code) {
        return dsl.select(CURRENCY.ID)
                .from(CURRENCY)
                .where(CURRENCY.CODE.equalIgnoreCase(code.toString().toUpperCase()))
                .fetchSingleInto(BigInteger.class);
    }

    //todo JavaDoc
    public CurrencyCode getCodeById(BigInteger id) {
        return dsl.select(CURRENCY.CODE)
                .from(CURRENCY)
                .where(CURRENCY.ID.equal(id))
                .fetchSingleInto(CurrencyCode.class);
    }
}
