package com.andriyuk.backendtest.api.v0_1;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

//todo Выделить API в отдельный проект
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

//todo JavaDoc
@Validated
public interface AccountOperations {

    @Get(processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns list of all accounts")
    List<Account> getList();

    @Get(uri = "/{id}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns account by specified id")
    Account getById(@Parameter(description = "Account ID") BigInteger id);

    @Put(consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Adds a new open account by template")
    Account add(@Parameter(description = "Template for account being adding") AccountTemplate accountTemplate);

    @Post(uri = "/close/{id}", consumes = MediaType.TEXT_PLAIN, processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Closes account by specified id")
    Account close(@Parameter(description = "Account ID") BigInteger id);

    @Post(uri = "/{id}/withdraw/{amount}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Withdraws specified amount of money from specified account")
    Account withdraw(@Parameter(description = "Account ID") BigInteger id,
                     @Parameter(description = "Amount of money") BigDecimal amount);

    @Post(uri = "/{id}/deposit/{amount}", consumes = MediaType.TEXT_PLAIN, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Deposits specified amount of money from specified account")
    Account deposit(@Parameter(description = "Account ID") BigInteger id,
                     @Parameter(description = "Amount of money") BigDecimal amount);


    @Post(uri = "/transfer", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Transfer specified amount of money from one account to another")
    TransferResult transfer(@Parameter(description = "Request for money transfer") TransferRequest transferRequest);
}
