package dk.hindsholm.bank.entity;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

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
