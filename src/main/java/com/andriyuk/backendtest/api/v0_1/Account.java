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
public class Account extends AccountTemplate {

    protected BigInteger id;
    protected AccountState state;

    @JsonCreator
    public Account(@JsonProperty("id") BigInteger id, @JsonProperty("userId") BigInteger userId,
                   @JsonProperty("number") String number, @JsonProperty("balance") BigDecimal balance,
                   @JsonProperty("currency") Currency currency, @JsonProperty("state") AccountState state) {
        super(userId, number, balance, currency);
        this.id = id;
        this.state = state;
    }

    @NotBlank
    public BigInteger getId() {
        return id;
    }

    @NotBlank
    public AccountState getState() {
        return state;
    }
}
