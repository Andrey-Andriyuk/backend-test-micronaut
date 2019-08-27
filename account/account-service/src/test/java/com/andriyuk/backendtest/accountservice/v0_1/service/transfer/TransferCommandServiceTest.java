package com.andriyuk.backendtest.accountservice.v0_1.service.transfer;

import com.andriyuk.backendtest.accountapi.v0_1.account.Account;
import com.andriyuk.backendtest.accountapi.v0_1.account.Currency;
import com.andriyuk.backendtest.accountapi.v0_1.transfer.TransferRequest;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountCommandService;
import com.andriyuk.backendtest.accountservice.v0_1.service.account.AccountQueryService;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static com.andriyuk.backendtest.accountservice.v0_1.service.TestHelper.getRandomAccountTemplate;
import static com.andriyuk.backendtest.accountservice.v0_1.service.TestHelper.getRandomBigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class TransferCommandServiceTest {

    @Inject
    AccountQueryService accountQueryService;
    @Inject
    AccountCommandService accountCommandService;

    @Inject
    TransferCommandService transferCommandService;

    @MicronautTest
    @Test
    public void transferTest() {
        BigDecimal transferAmount = getRandomBigDecimal();
        Account sourceAccount = accountCommandService.create(getRandomAccountTemplate(transferAmount, Currency.EUR));
        Account destinationAccount = accountCommandService.create(getRandomAccountTemplate(getRandomBigDecimal(), Currency.EUR));
        Account otherAccount = accountCommandService.create(getRandomAccountTemplate());

        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, destinationAccount, BigDecimal.ZERO)),
                "Client shouldn't be able to create zero amount of money.");
        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, destinationAccount, BigDecimal.ONE.negate())),
                "Client shouldn't be able to create negative amount of money.");

        Account emptyAccount = accountCommandService.create(getRandomAccountTemplate(BigDecimal.ZERO, Currency.EUR));
        accountCommandService.close(emptyAccount.getId());
        assertThrows(IllegalStateException.class,
                () -> transferCommandService.create(new TransferRequest(emptyAccount, destinationAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create money from closed account.");
        assertThrows(IllegalStateException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, emptyAccount, BigDecimal.ONE)),
                "Client shouldn't be able to create money to closed account.");

        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, destinationAccount, transferAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to create extra money.");

        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, destinationAccount, transferAmount.multiply(BigDecimal.TEN))),
                "Client shouldn't be able to create extra money.");

        Account differentCurrencyAccount = accountCommandService.create(getRandomAccountTemplate(getRandomBigDecimal(), Currency.USD));
        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(sourceAccount, differentCurrencyAccount, transferAmount)),
                "Client shouldn't be able to create money between accounts with different currencies.");
        assertThrows(IllegalArgumentException.class,
                () -> transferCommandService.create(new TransferRequest(differentCurrencyAccount, sourceAccount, transferAmount)),
                "Client shouldn't be able to create money between accounts with different currencies.");

        transferCommandService.create(new TransferRequest(sourceAccount, destinationAccount, transferAmount));
        Account sourceAccountChanged = accountQueryService.getById(sourceAccount.getId());
        Account destinationAccountChanged = accountQueryService.getById(destinationAccount.getId());
        assertEquals(destinationAccountChanged.getBalance(), destinationAccount.getBalance().add(transferAmount),
                "Destination account balance should increase by specified amount");
        assertEquals(sourceAccountChanged.getBalance(), sourceAccount.getBalance().subtract(transferAmount),
                "Source account balance should decrease by specified amount");

        assertEquals(otherAccount, accountQueryService.getById(otherAccount.getId()),
                "Rest accounts should remain unchanged.");
    }

}
