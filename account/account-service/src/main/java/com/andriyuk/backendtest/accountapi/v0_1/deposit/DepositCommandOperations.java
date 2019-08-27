package com.andriyuk.backendtest.accountapi.v0_1.deposit;

import com.andriyuk.backendtest.accountapi.v0_1.account.AccountQueryOperations;
import com.andriyuk.backendtest.accountapi.v0_1.account.BalanceChangeRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigInteger;

/**
 * Deposit service API
 */
@Validated
@Controller(AccountQueryOperations.ACCOUNTS_RESOURCES_PATH)
public interface DepositCommandOperations {

    /**
     * Creates deposit for specified account
     * @param accountId              account id
     * @param balanceChangeRequest   deposit model
     */
    @Post(uri = "/{accountId}/deposits", consumes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates deposit")
    void create(@Parameter(description = "Account ID") BigInteger accountId,
                @Parameter(description = "Deposit model") BalanceChangeRequest balanceChangeRequest);
}
