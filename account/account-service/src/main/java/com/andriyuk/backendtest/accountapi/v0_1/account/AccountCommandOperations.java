package com.andriyuk.backendtest.accountapi.v0_1.account;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigInteger;

/**
 * Account command operations API
 */
@Validated
@Controller(AccountQueryOperations.ACCOUNTS_RESOURCES_PATH)
public interface AccountCommandOperations {

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
