package com.andriyuk.backendtest.api.v0_1.transfer;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * Accounts service API
 */
@Validated
@Controller("/api/v0_1/transfers")
public interface TransferOperations {
    /**
     * Create transfer from one account to another
     * @param transferRequest   request model for money create
     */
    @Post(uri = "/transfers", consumes = MediaType.APPLICATION_JSON)
    @Operation(summary = "Create transfer from one account to another")
    void create(@Parameter(description = "Transfer model") TransferRequest transferRequest);
}
