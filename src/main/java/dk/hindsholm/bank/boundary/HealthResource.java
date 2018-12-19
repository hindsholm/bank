package dk.hindsholm.bank.boundary;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("health")
public class HealthResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "OK";
    }

    @OPTIONS
    @Produces(MediaType.TEXT_PLAIN)
    public String options() {
        return "OK";
    }

}
