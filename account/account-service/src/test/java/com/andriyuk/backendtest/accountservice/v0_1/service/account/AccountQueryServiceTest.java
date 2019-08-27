package com.andriyuk.backendtest.accountservice.v0_1.service.account;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AccountQueryServiceTest {

    @Inject
    AccountQueryService accountQueryService;

    @MicronautTest
    @Test
    public void getByInvalidIdTest() {
        assertThrows(IllegalArgumentException.class,
                //Account with big negative id definitely doesn't exist
                () -> accountQueryService.getById(BigInteger.valueOf(Long.MIN_VALUE)),
                "Client shouldn't be able to query non existent account.");
    }
}
