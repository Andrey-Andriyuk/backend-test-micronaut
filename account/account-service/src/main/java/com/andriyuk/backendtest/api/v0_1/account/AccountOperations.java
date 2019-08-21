package com.andriyuk.backendtest.api.v0_1.account;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

//todo Move API to separate project
import java.math.BigInteger;
import java.util.List;

import static com.andriyuk.backendtest.api.v0_1.account.AccountOperations.ACCOUNTS_RESOURCES_PATH;

/**
 * Accounts service API
 */
@Validated
@Controller(ACCOUNTS_RESOURCES_PATH)
public interface AccountOperations {

    String ACCOUNTS_RESOURCES_PATH = "/api/v0_1/accounts";

    /**
     * Returns list of all accounts
     * @return list of account models
     */
    @Get(processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns list of all accounts")
    List<Account> getList();

    /**
     * Returns account by specified id
     * @param accountId    account id
     * @return      account model
     */
    @Get(uri = "/{accountId}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns account by specified id")
    Account getById(@Parameter(description = "Account ID") BigInteger accountId);

    //todo operation for querying account by user

    /**
     * Creates a new account by template
     * @param accountTemplate   template of account to create
     * @return                  added account model
     */
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new account by template")
    Account create(@Parameter(description = "Template for account being creating") AccountTemplate accountTemplate);

    /**
     * Closes account by specified id
     * @param accountId   account id
     */
    @Delete(uri = "/{accountId}", consumes = MediaType.TEXT_PLAIN)
    @Operation(summary = "Closes specified account")
    void close(@Parameter(description = "Account ID") BigInteger accountId);
}
