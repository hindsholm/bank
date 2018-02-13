package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import java.util.Arrays;
import java.util.Optional;
import javax.json.JsonObject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {

    @Mock
    AccountAdministration admin;

    @InjectMocks
    AccountResource service;
    
    @Mock
    UriInfo uriInfo;
    
    @Mock
    Request request;
    
    @Spy
    EntityBuilder entityBuilder;

    @Before
    public void before() {
        when(uriInfo.getBaseUriBuilder()).thenReturn(UriBuilder.fromPath("http://mock"));        
    }
    
    @Test
    public void testList() {
        when(admin.listAccounts())
            .thenReturn(Arrays.asList(new Account("5479", "1", "Checking account"), new Account("5479", "2", "Savings account")));

        JsonObject json = (JsonObject) service.list(uriInfo, request).getEntity();

        assertEquals(2, json.getJsonObject("_embedded").getJsonArray("accounts").size());
        assertEquals("http://mock/accounts", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testGet() {
        when(admin.findAccount("5479", "1234")).thenReturn(Optional.of(new Account("5479", "1234", "Savings account")));

        JsonObject json = (JsonObject) service.get("5479", "1234", uriInfo, request).getEntity();

        assertEquals("5479", json.getString("regNo"));
        assertEquals("1234", json.getString("accountNo"));
        assertEquals("http://mock/accounts/5479-1234", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testCreate() {
        AccountUpdate accountUpdate = mock(AccountUpdate.class);
        when(accountUpdate.getName()).thenReturn("new Account");
        when(accountUpdate.getRegNo()).thenReturn("5479");
        when(accountUpdate.getAccountNo()).thenReturn("12345678");
        when(admin.findAccount("5479", "12345678")).thenReturn(Optional.empty());

        JsonObject json = (JsonObject) service.createOrUpdate("5479", "12345678", accountUpdate, uriInfo, request).getEntity();

        assertEquals("new Account", json.getString("name"));
        assertEquals("5479", json.getString("regNo"));
        assertEquals("12345678", json.getString("accountNo"));
        assertEquals("http://mock/accounts/5479-12345678", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testUpdate() {
        Account account = new Account("5479", "12345678", "Savings account");
        when(admin.findAccount("5479", "12345678")).thenReturn(Optional.of(account));
        AccountUpdate accountUpdate = mock(AccountUpdate.class);
        when(accountUpdate.getName()).thenReturn("new name");
        when(accountUpdate.getRegNo()).thenReturn("5479");
        when(accountUpdate.getAccountNo()).thenReturn("12345678");

        JsonObject json = (JsonObject) service.createOrUpdate("5479", "12345678", accountUpdate, uriInfo, request).getEntity();

        assertEquals("new name", account.getName());
        assertEquals("new name", json.getString("name"));
        assertEquals("5479", json.getString("regNo"));
        assertEquals("12345678", json.getString("accountNo"));
        assertEquals("http://mock/accounts/5479-12345678", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test(expected = BadInputException.class)
    public void testCreateInvalidRequest() throws Exception {
        AccountUpdate accountUpdate = mock(AccountUpdate.class);
        when(accountUpdate.getRegNo()).thenReturn("5479");
        when(accountUpdate.getAccountNo()).thenReturn("12345678");
        service.createOrUpdate("5479", "87654321", accountUpdate, uriInfo, request);
    }
}
