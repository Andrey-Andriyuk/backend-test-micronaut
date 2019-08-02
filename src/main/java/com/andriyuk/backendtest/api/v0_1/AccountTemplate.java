package com.andriyuk.backendtest.api.v0_1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;

@Immutable
@Validated
public class AccountTemplate {

    protected BigInteger userId;
    protected String number;
    protected BigDecimal balance;
    protected Currency currency;

    @JsonCreator
    public AccountTemplate(@JsonProperty("userId") BigInteger userId,
                   @JsonProperty("number") String number, @JsonProperty("balance") BigDecimal balance,
                   @JsonProperty("currency") Currency currency) {
        this.userId = userId;
        this.number = number;
        this.balance = balance;
        this.currency = currency;
    }

    @NotBlank
    public BigInteger getUserId() {
        return userId;
    }

    @NotBlank
    public String getNumber() {
        return number;
    }

    @NotBlank
    public BigDecimal getBalance() {
        return balance;
    }

    @NotBlank
    public Currency getCurrency() {
        return currency;
    }
}
