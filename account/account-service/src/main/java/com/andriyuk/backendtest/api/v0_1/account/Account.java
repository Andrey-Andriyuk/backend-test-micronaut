package com.andriyuk.backendtest.api.v0_1.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Account model
 */
@Immutable
@Validated
public class Account extends AccountTemplate {

    public static final byte IBAN_MAX_ACCOUNT_NUMBER_LENGTH = 34;

    /**
     * Account id
     */
    protected BigInteger id;

    /**
     * Account state
     */
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

    @Override
    public boolean equals(Object obj) {
        //Checking accounts for equality involves all Account object fields
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
