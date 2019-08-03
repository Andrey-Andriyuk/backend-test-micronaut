package com.andriyuk.backendtest.api.v0_1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;

//todo JavaDoc
@Immutable
@Validated
public class TransferRequest {

    private BigInteger sourceAccountId;
    private BigInteger destinationAccountId;
    private BigDecimal amount;

    @JsonCreator
    public TransferRequest(@JsonProperty("sourceAccountId") BigInteger sourceAccountId,
                           @JsonProperty("destinationAccountId") BigInteger destinationAccountId,
                           @JsonProperty("amount") BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    @NotBlank
    public BigInteger getSourceAccountId() {
        return sourceAccountId;
    }

    @NotBlank
    public BigInteger getDestinationAccountId() {
        return destinationAccountId;
    }

    @NotBlank
    public BigDecimal getAmount() {
        return amount;
    }
}
