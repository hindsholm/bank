package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/accounts")
@PermitAll
@DeclareRoles("advisor")
public class AccountResource {

    @EJB
    private AccountAdministration admin;

    @Inject
    EntityBuilder entityBuilder;

    /**
     * Lists all accounts.
     */
    @GET
    @Produces({"application/hal+json;v=1", "application/hal+json"})
    public Response list(@Context UriInfo uriInfo, @Context Request request) {
        List<Account> accounts = admin.listAccounts();
        CacheControl cc = new CacheControl();
        cc.setMaxAge(10);
        return Response.ok(entityBuilder.buildAccountsJson(accounts, uriInfo)).cacheControl(cc).build();
    }

    @GET
    @Path("{regNo}-{accountNo}")
    @Produces({"application/hal+json;v=1", "application/hal+json"})
    public Response get(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
            @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
            @Context UriInfo uriInfo, @Context Request request) {
        Account account = admin.getAccount(regNo, accountNo);
        CacheControl cc = new CacheControl();
        cc.setMaxAge(60);
        return Response.ok(entityBuilder.buildAccountJson(account, uriInfo)).cacheControl(cc).build();
    }

    @PUT
    @RolesAllowed("advisor")
    @Path("{regNo}-{accountNo}")
    @Produces({"application/hal+json"})
    @Consumes({MediaType.APPLICATION_JSON + ";v=1", MediaType.APPLICATION_JSON})
    public Response createOrUpdate(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
            @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
            @Valid AccountUpdate account,
            @Context UriInfo uriInfo, @Context Request request) {
        if (!regNo.equals(account.getRegNo()) || !accountNo.equals(account.getAccountNo())) {
            throw new BadInputException("Account in uri " + regNo + "-" + accountNo + " is not matching account in body " + 
                    account.getRegNo() + "-" + account.getAccountNo() + "!");
        }

        Optional<Account> acc = admin.findAccount(regNo, accountNo);
        Account a;
        if (acc.isPresent()) {
            a = acc.get();
            a.setName(account.getName());
        } else {
            a = new Account(regNo, accountNo, account.getName());
        }
        admin.save(a);
        CacheControl cc = new CacheControl();
        cc.setMaxAge(60);
        return Response.ok(entityBuilder.buildAccountJson(a, uriInfo)).cacheControl(cc).build();
    }

}
