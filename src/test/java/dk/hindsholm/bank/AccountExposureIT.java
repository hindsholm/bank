package dk.hindsholm.bank;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Ignore;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class AccountExposureIT {
    
    WebTarget target;
    
    @Before
    public void setup() {
        target = ClientBuilder.newClient().target("http://localhost:8080/bank/resources");
    }

    @Test
    public void testListAccounts() {
        Map<String, Object> response = target.path("accounts")
                .request()
                .accept("application/hal+json")
                .get(Map.class);

        assertNotNull(response.get("_embedded"));
        Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
        assertTrue(((List) embedded.get("accounts")).size() >= 2);
    }

    @Test
    public void testGetAccount() {
        Map<String, Object> response = target.path("accounts").path("5479-1234567")
                .request()
                .accept("application/hal+json")
                .get(Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("Checkings Account", response.get("name"));
    }

    @Ignore
    @Test
    public void testCreateAccount() throws Exception {
        int accountNo = ThreadLocalRandom.current().nextInt(9999999);

        Map<String, String> accountCreate = new ConcurrentHashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", Integer.toString(accountNo));
        accountCreate.put("name", "Savings account");
        Map<String, Object> response = target.path("accounts").path("5479-" + accountNo)
                .request()
                .accept("application/hal+json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals(Integer.toString(accountNo), response.get("accountNo"));
        assertEquals("Savings account", response.get("name"));
    }

    @Ignore
    @Test
    public void testUpdateAccount() throws Exception {
        Map<String, String> accountCreate = new ConcurrentHashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "1234567");
        accountCreate.put("name", "new account name");
        Map<String, Object> response = target.path("accounts").path("5479-" + "1234567")
                .request()
                .accept("application/hal+json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("new account name", response.get("name"));
    }

}
