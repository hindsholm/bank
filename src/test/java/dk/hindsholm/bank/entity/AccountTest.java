package dk.hindsholm.bank.entity;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    public void testAddTransaction() {
        Account account = new Account("5479", "123456", "Savings account");
        account.addTransaction("description", new BigDecimal("1234.42"));

        assertEquals(1, account.getTransactions().size());
        Transaction transaction = account.getTransactions().iterator().next();
        assertEquals("description", transaction.getDescription());
        assertEquals(new BigDecimal("1234.42"), transaction.getAmount());
    }
}
