package com.andriyuk.backendtest.api.v0_1;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

//todo Move API to separate project
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Account service API
 */
@Validated
@Controller("/v0_1")
public interface AccountOperations {

    /**
     * Returns list of all accounts
     * @return list of account models
     */
    @Get(processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns list of all accounts")
    List<Account> getList();

    /**
     * Returns account by specified id
     * @param id    account id
     * @return      account model
     */
    @Get(uri = "/{id}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns account by specified id")
    Account getById(@Parameter(description = "Account ID") BigInteger id);

    /**
     * Adds a new open account by template
     * @param accountTemplate   template of account to add
     * @return                  added account model
     */
    @Post(consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Adds a new open account by template")
    Account add(@Parameter(description = "Template for account being adding") AccountTemplate accountTemplate);

    /**
     * Close account by specified id
     * @param id        account id
     * @return          closed   account model
     */
    @Put(uri = "/close/{id}", consumes = MediaType.TEXT_PLAIN, processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Close account by specified id")
    Account close(@Parameter(description = "Account ID") BigInteger id);

    /**
     * Withdraws specified amount of money from specified account
     * @param id        account id
     * @param amount    amount to withdraw
     * @return          model of modified account
     */
    @Post(uri = "/{id}/withdraw/{amount}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Withdraws specified amount of money from specified account")
    Account withdraw(@Parameter(description = "Account ID") BigInteger id,
                     @Parameter(description = "Amount to withdraw") BigDecimal amount);

    /**
     * Deposits specified amount of money from specified account
     * @param id        account id
     * @param amount    amount to deposit
     * @return          model of modified account
     */
    @Post(uri = "/{id}/deposit/{amount}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Deposits specified amount of money from specified account")
    Account deposit(@Parameter(description = "Account ID") BigInteger id,
                     @Parameter(description = "Amount to deposit") BigDecimal amount);


    /**
     * Transfer specified amount of money from one account to another
     * @param transferRequest   request model for money transfer
     * @return                  result model of money transfer
     */
    @Post(uri = "/transfer", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Transfer specified amount of money from one account to another")
    TransferResult transfer(@Parameter(description = "Request for money transfer") TransferRequest transferRequest);
}
