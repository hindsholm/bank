package dk.hindsholm.bank;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;

import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;
import pl.domzal.junit.docker.rule.WaitFor;

public class AccountExposureIT {

    @ClassRule
    public static DockerRule container = DockerRule.builder()
            .imageName("bank")
            .waitFor(WaitFor.logMessage("bank was successfully deployed"))
            .build();

    WebTarget target;

    @Before
    public void setup() {
        String dockerUrl = "http://" + container.getDockerHost() + ":" + container.getExposedContainerPort("8080") + "/bank/resources";
        target = ClientBuilder.newClient().target(dockerUrl);
    }

    @Test
    public void testListAccounts() {
        Map response = target.path("accounts")
                .request()
                .accept("application/hal+json")
                .get(Map.class);

        assertNotNull(response.get("_embedded"));
        Map embedded = (Map) response.get("_embedded");
        assertTrue(((List) embedded.get("accounts")).size() >= 2);
    }

    @Test
    public void testGetAccount() {
        Map response = target.path("accounts").path("5479-1234567")
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
        Map response = target.path("accounts").path("5479-" + accountNo)
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
        Map response = target.path("accounts").path("5479-" + "1234567")
                .request()
                .accept("application/hal+json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("new account name", response.get("name"));
    }

}
