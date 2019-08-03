package com.andriyuk.backendtest.api.v0_1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;

/**
 * Domain model of money transfer result
 */
@Immutable
@Validated
public class TransferResult {

    /**
     * Domain model of modified source account
     */
    private Account sourceAccount;

    /**
     * Domain model of modified destination account
     */
    private Account destinationAccount;

    @JsonCreator
    public TransferResult(@JsonProperty("sourceAccount") Account sourceAccount,
                          @JsonProperty("destinationAccount") Account destinationAccount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
    }

    @NotBlank
    public Account getSourceAccount() {
        return sourceAccount;
    }

    @NotBlank
    public Account getDestinationAccount() {
        return destinationAccount;
    }
}
