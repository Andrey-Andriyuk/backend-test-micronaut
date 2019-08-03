package com.andriyuk.backendtest.api.v0_1;

import java.util.Random;

//todo JavaDoc
public enum  Currency {
    USD,
    EUR,
    RUR; //..etc

    //todo JavaDoc
    public static Currency getRandom() {
        return  values()[(new Random()).nextInt(values().length)];
    }
}
