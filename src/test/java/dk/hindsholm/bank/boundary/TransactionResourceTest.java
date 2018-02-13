package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import dk.hindsholm.bank.entity.Transaction;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collections;

import javax.json.JsonObject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionResourceTest {

    @Mock
    AccountAdministration admin;
    
    @Spy
    EntityBuilder entityBuilder;

    @InjectMocks
    TransactionResource service;

    @Mock
    Request request;
    
    @Mock
    UriInfo uriInfo;
    
    @Before
    public void before() {
        when(uriInfo.getBaseUriBuilder()).thenReturn(UriBuilder.fromPath("http://mock"));        
    }
    
    @Test
    public void testList() {
        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        when(account.getTransactions()).thenReturn(Collections.singleton(new Transaction(account, new BigDecimal("1234.42"), "description")));
        when(admin.getAccount("5479", "123456")).thenReturn(account);

        JsonObject json = (JsonObject) service.list("5479", "123456", uriInfo, request).getEntity();

        assertEquals(1, json.getJsonObject("_embedded").getJsonArray("transactions").size());
        assertEquals("http://mock/accounts/5479-123456/transactions", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testGet() {
        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        Transaction dbTranscation = new Transaction(account, new BigDecimal("1234.42"), "description");
        when(admin.getTransaction("5479", "123456", "xxx-yyy")).thenReturn(dbTranscation);

        JsonObject json = (JsonObject) service.get("5479", "123456", "xxx-yyy", uriInfo, request).getEntity();

        assertEquals(new BigDecimal("1234.42"), json.getJsonNumber("amount").bigDecimalValue());
        assertEquals("http://mock/accounts/5479-123456/transactions/" + dbTranscation.getId(), 
                json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

}
