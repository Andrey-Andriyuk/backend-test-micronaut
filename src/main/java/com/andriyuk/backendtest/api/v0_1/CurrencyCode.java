package com.andriyuk.backendtest.api.v0_1;

import java.util.Random;

/**
 * CurrencyCode model
 */
public enum CurrencyCode {
    USD,
    EUR,
    RUR; //..etc

    /**
     * Returns random currencyCode
     * @return currencyCode
     */
    public static CurrencyCode getRandom() {
        return  values()[(new Random()).nextInt(values().length)];
    }
}
