package com.andriyuk.backendtest.api.v0_1.transfer;

import com.andriyuk.backendtest.api.v0_1.account.Account;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Model of money create
 */
@Immutable
@Validated
public class TransferRequest {

    /**
     * Source account model
     */
    private Account sourceAccount;

    /**
     * Destination account id
     */
    private Account destinationAccount;

    /**
     * Transfer amount
     */
    private BigDecimal amount;

    @JsonCreator
    public TransferRequest(@JsonProperty("sourceAccount") Account sourceAccount,
                           @JsonProperty("destinationAccount") Account destinationAccount,
                           @JsonProperty("amount") BigDecimal amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    @NotBlank
    public Account getSourceAccount() {
        return sourceAccount;
    }

    @NotBlank
    public Account getDestinationAccount() {
        return destinationAccount;
    }

    @NotBlank
    public BigDecimal getAmount() {
        return amount;
    }
}
