package dk.hindsholm.bank;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("rawtypes")
@Testcontainers
public class AccountExposureIT {

    @Container
    static final GenericContainer CONTAINER = new GenericContainer(
            new ImageFromDockerfile("bank")
                    .withFileFromPath("./src/test/payara", Paths.get("./src/test/payara"))
                    .withFileFromPath("./target/bank.war", Paths.get("./target/bank.war"))
                    .withFileFromPath("Dockerfile", Paths.get("Dockerfile")))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/bank/resources/health"));

    String dockerUrl;

    @BeforeEach
    public void setup() {
        dockerUrl = "http://" + CONTAINER.getContainerIpAddress() + ":" + CONTAINER.getMappedPort(8080) + "/bank/resources";
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

    @Test
    public void testUnknownUser() {
        Map<String, String> accountCreate = new HashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "123456");
        accountCreate.put("name", "Savings account");
        WebTarget target = ClientBuilder.newClient().target(dockerUrl);
        NotFoundException e = assertThrows(NotFoundException.class, () -> target.path("accounts").path("dummy")
                .request()
                .accept("application/json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class));
        assertTrue(e.getMessage().contains("404"));
    }

    @Test
    public void testUserNotInRequiredGroup() {
        Map<String, String> accountCreate = new HashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "123456");
        accountCreate.put("name", "Savings account");
        WebTarget target = ClientBuilder.newClient().register(new Authenticator("cust1", "passw0rd")).target(dockerUrl);
        NotFoundException e = assertThrows(NotFoundException.class, () -> target.path("accounts").path("dummy")
                .request()
                .accept("application/json")
                .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class));
        assertTrue(e.getMessage().contains("404"));
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
