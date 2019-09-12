package com.andriyuk.backendtest.api.v0_1.account;

import com.andriyuk.backendtest.api.v0_1.account.AccountTemplate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.validation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * Balance change request model
 */
@Immutable
@Validated
public class BalanceChangeRequest {

    /**
     * Description of account to change. Used to ensure that account wasn't changed elsewhere.
     */
    private AccountTemplate accountDescription;


    /**
     * Balance change amount. Depending on operation it may be  amount of withdrawing or deposing.
     */
    private BigDecimal amount;

    @JsonCreator
    public BalanceChangeRequest(@JsonProperty("accountDescription") AccountTemplate accountDescription,
                                @JsonProperty("amount") BigDecimal amount) {
        this.accountDescription = accountDescription;
        this.amount = amount;
    }

    @NotBlank
    public AccountTemplate getAccountDescription() {
        return accountDescription;
    }

    @NotBlank
    public BigDecimal getAmount() {
        return amount;
    }
}
