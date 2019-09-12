package com.andriyuk.backendtest.api.v0_1;

import com.andriyuk.backendtest.api.v0_1.account.BalanceChangeRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigInteger;

import static com.andriyuk.backendtest.api.v0_1.account.AccountOperations.ACCOUNTS_RESOURCES_PATH;

/**
 * Deposit service API
 */
@Validated
@Controller(ACCOUNTS_RESOURCES_PATH)
public interface DepositOperations {

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
