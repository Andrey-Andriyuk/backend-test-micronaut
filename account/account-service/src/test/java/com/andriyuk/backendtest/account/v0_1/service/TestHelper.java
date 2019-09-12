package com.andriyuk.backendtest.account.v0_1.service;

import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.andriyuk.backendtest.api.v0_1.account.AccountTemplate;
import com.andriyuk.backendtest.api.v0_1.account.Currency;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public abstract class TestHelper {
    public static BigInteger getRandomBigInteger() {
        return new BigInteger(32, new Random());
    }

    public static BigDecimal getRandomBigDecimal() {
        return new BigDecimal(Math.abs(Math.random()) * 100000);
    }

    public static AccountTemplate getRandomAccountTemplate(String number) {
        return new AccountTemplate(getRandomBigInteger(), number, getRandomBigDecimal(), Currency.getRandom());
    }

    public static AccountTemplate getRandomAccountTemplate(BigDecimal balance) {
        return getRandomAccountTemplate(balance, Currency.getRandom());
    }

    public static AccountTemplate getRandomAccountTemplate(BigDecimal balance, Currency currency) {
        return new AccountTemplate(getRandomBigInteger(),
                RandomStringUtils.random(Account.IBAN_MAX_ACCOUNT_NUMBER_LENGTH, true, true),
                balance, currency);
    }
    public static AccountTemplate getRandomAccountTemplate() {
        return getRandomAccountTemplate(getRandomBigDecimal());
    }
}
