package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import dk.hindsholm.bank.entity.Transaction;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import javax.json.JsonObject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionResourceTest {

    @Mock
    AccountAdministration admin;
    
    @Spy
    EntityBuilder entityBuilder;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    TransactionResource service;

    @BeforeEach
    public void before() {
        when(uriInfo.getBaseUriBuilder()).thenReturn(UriBuilder.fromPath("http://mock"));        
    }
    
    @Test
    public void testList() {
        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        when(account.getTransactions())
                .thenReturn(Collections.singleton(new Transaction(account, new BigDecimal("1234.42"), "description")));
        when(admin.findAccount("5479", "123456")).thenReturn(Optional.of(account));

        JsonObject json = (JsonObject) service.list("5479", "123456").getEntity();

        assertEquals(1, json.getJsonObject("_embedded").getJsonArray("transactions").size());
        assertEquals("http://mock/accounts/5479-123456/transactions",
                json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testGet() {
        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        Transaction dbTranscation = new Transaction(account, new BigDecimal("1234.42"), "description");
        when(admin.findTransaction("5479", "123456", "xxx-yyy")).thenReturn(Optional.of(dbTranscation));

        JsonObject json = (JsonObject) service.get("5479", "123456", "xxx-yyy").getEntity();

        assertEquals(new BigDecimal("1234.42"), json.getJsonNumber("amount").bigDecimalValue());
        assertEquals("http://mock/accounts/5479-123456/transactions/" + dbTranscation.getId(), 
                json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

}
