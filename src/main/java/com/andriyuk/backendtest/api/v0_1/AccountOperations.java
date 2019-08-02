package com.andriyuk.backendtest.api.v0_1;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

//todo Выделить API в отдельный проект
import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
public interface AccountOperations {

    @Get(processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns list of all accounts")
    List<Account> getList();

    @Put(uri = "/add", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Adds a new account by template")
    Account add(@Parameter(description = "Template for account being adding") @NotBlank AccountTemplate accountTemplate);
}
