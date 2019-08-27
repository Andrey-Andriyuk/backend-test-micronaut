package com.andriyuk.backendtest.accountapi.v0_1.account;

import java.util.Random;

/**
 * Currency model
 */
public enum  Currency {
    USD,
    EUR,
    RUR; //..etc

    /**
     * Returns random currency
     * @return currency
     */
    public static Currency getRandom() {
        return  values()[(new Random()).nextInt(values().length)];
    }
}
