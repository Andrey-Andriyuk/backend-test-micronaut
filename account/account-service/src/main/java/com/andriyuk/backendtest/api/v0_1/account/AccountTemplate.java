package com.andriyuk.backendtest.api.v0_1.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Account template model
 */
@Immutable
@Validated
public class AccountTemplate {

    /**
     * User id
     */
    protected BigInteger userId;

    /**
     * Account number
     */
    protected String number;

    /**
     * Initial account balance
     */
    protected BigDecimal balance;

    /**
     * Account currency
     */
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AccountTemplate)) {
            return false;
        }

        AccountTemplate that = (AccountTemplate) obj;
        //Implementing AccountTemplate#equals this way to be able to compare AccountTemplate with Account
        return (new EqualsBuilder())
                .append(this.getUserId(), that.getUserId())
                .append(this.getNumber(), that.getNumber())
                .append(this.getBalance(), that.getBalance())
                .append(this.getCurrency(), that.getCurrency())
                .build();
    }

    @Override
    public int hashCode() {
        //Accounts, belonging to same user, will be stored in the same bucket.
        return (new HashCodeBuilder()).append(getUserId()).build();
    }
}
