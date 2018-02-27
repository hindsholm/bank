package dk.hindsholm.bank;

import java.util.HashMap;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.ClassRule;

import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;
import pl.domzal.junit.docker.rule.WaitFor;

public class AccountExposureIT {

    @ClassRule
    // Start the Docker container
    public static DockerRule container = DockerRule.builder()
            .imageName("bank")
            .waitFor(WaitFor.logMessage("bank was successfully deployed"))
            .build();

    String dockerUrl;

    @Before
    public void setup() {
        dockerUrl = "http://" + container.getDockerHost() + ":" + container.getExposedContainerPort("8080") + "/bank/resources";
    }

    @Test
    public void testListAccounts() {
        WebTarget target = ClientBuilder.newClient().target(dockerUrl);
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
        WebTarget target = ClientBuilder.newClient().target(dockerUrl);
        Map response = target.path("accounts").path("5479-1234567")
                .request()
                .accept("application/hal+json")
                .get(Map.class);
        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("Checkings Account", response.get("name"));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUnknownUser() {
        Map<String, String> accountCreate = new HashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "123456");
        accountCreate.put("name", "Savings account");
        WebTarget target = ClientBuilder.newClient().target(dockerUrl);
        target.path("accounts").path("dummy")
                .request()
                .accept("application/json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);
    }

    @Test(expected = ForbiddenException.class)
    public void testUserNotInRequiredGroup() {
        Map<String, String> accountCreate = new HashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "123456");
        accountCreate.put("name", "Savings account");
        WebTarget target = ClientBuilder.newClient().register(new Authenticator("cust1", "passw0rd")).target(dockerUrl);
        target.path("accounts").path("dummy")
                .request()
                .accept("application/json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);
    }

    @Test
    public void testCreateAccount() {
        int accountNo = ThreadLocalRandom.current().nextInt(9999999);
        WebTarget target = ClientBuilder.newClient().register(new Authenticator("adv1", "passw0rd")).target(dockerUrl);
        Map<String, String> accountCreate = new HashMap<>();
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

    @Test
    public void testUpdateAccount() {
        WebTarget target = ClientBuilder.newClient().register(new Authenticator("adv1", "passw0rd")).target(dockerUrl);
        Map<String, String> accountUpdate = new HashMap<>();
        accountUpdate.put("regNo", "5479");
        accountUpdate.put("accountNo", "1234567");
        accountUpdate.put("name", "new account name");
        Map response = target.path("accounts").path("5479-" + "1234567")
                .request()
                .accept("application/hal+json")
                .put(Entity.entity(accountUpdate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("new account name", response.get("name"));
    }

}
