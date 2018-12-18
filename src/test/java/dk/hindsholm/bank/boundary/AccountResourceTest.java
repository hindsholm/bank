package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import java.util.Arrays;
import java.util.Optional;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountResourceTest {

    @Mock
    AccountAdministration admin;

    @Mock
    UriInfo uriInfo;

    @Spy
    EntityBuilder entityBuilder;

    @InjectMocks
    AccountResource service;

    @BeforeEach
    public void before() {
        when(uriInfo.getBaseUriBuilder()).thenReturn(UriBuilder.fromPath("http://mock"));        
    }
    
    @Test
    public void testList() {
        when(admin.listAccounts())
            .thenReturn(Arrays.asList(new Account("5479", "1", "Checking account"), new Account("5479", "2", "Savings account")));

        JsonObject json = (JsonObject) service.list().getEntity();

        assertEquals(2, json.getJsonObject("_embedded").getJsonArray("accounts").size());
        assertEquals("http://mock/accounts", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testGet() {
        when(admin.findAccount("5479", "1234")).thenReturn(Optional.of(new Account("5479", "1234", "Savings account")));

        JsonObject json = (JsonObject) service.get("5479", "1234").getEntity();

        assertEquals("5479", json.getString("regNo"));
        assertEquals("1234", json.getString("accountNo"));
        assertEquals("http://mock/accounts/5479-1234", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testGetNotFound() {
        when(admin.findAccount("5479", "1234")).thenReturn(Optional.empty());

        Response response = service.get("5479", "1234");

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testCreate() {
        AccountUpdate accountUpdate = mock(AccountUpdate.class);
        when(accountUpdate.getName()).thenReturn("new Account");
        when(accountUpdate.getRegNo()).thenReturn("5479");
        when(accountUpdate.getAccountNo()).thenReturn("12345678");
        when(admin.findAccount("5479", "12345678")).thenReturn(Optional.empty());

        JsonObject json = (JsonObject) service.createOrUpdate("5479", "12345678", accountUpdate).getEntity();

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

        JsonObject json = (JsonObject) service.createOrUpdate("5479", "12345678", accountUpdate).getEntity();

        assertEquals("new name", account.getName());
        assertEquals("new name", json.getString("name"));
        assertEquals("5479", json.getString("regNo"));
        assertEquals("12345678", json.getString("accountNo"));
        assertEquals("http://mock/accounts/5479-12345678", json.getJsonObject("_links").getJsonObject("self").getString("href"));
    }

    @Test
    public void testCreateInvalidRequest() throws Exception {
        AccountUpdate accountUpdate = mock(AccountUpdate.class);
        when(accountUpdate.getRegNo()).thenReturn("5479");
        when(accountUpdate.getAccountNo()).thenReturn("12345678");
        Throwable exception = Assertions.assertThrows(BadInputException.class, 
                () -> service.createOrUpdate("5479", "87654321", accountUpdate));
        Assertions.assertTrue(exception.getMessage().contains("is not matching"));
    }
}
