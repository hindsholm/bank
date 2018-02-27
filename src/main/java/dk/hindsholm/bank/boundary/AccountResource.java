package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.DeclareRoles;
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
    @Produces("application/hal+json")
    public Response list(@Context UriInfo uriInfo, @Context Request request) {
        List<Account> accounts = admin.listAccounts();
        CacheControl cc = new CacheControl();
        cc.setMaxAge(10);
        return Response.ok(entityBuilder.buildAccountsJson(accounts, uriInfo)).cacheControl(cc).build();
    }

    /**
     * GETs a specific account.
     * If the account does not exist, 404 is returned.
     */
    @GET
    @Path("{regNo}-{accountNo}")
    @Produces("application/hal+json")
    public Response get(@PathParam("regNo") @Pattern(regexp = "^\\d{4}$") String regNo,
            @PathParam("accountNo") @Pattern(regexp = "^\\d+$") String accountNo,
            @Context UriInfo uriInfo, @Context Request request) {
        Optional<Account> account = admin.findAccount(regNo, accountNo);
        if (account.isPresent()) {
            CacheControl cc = new CacheControl();
            cc.setMaxAge(60);
            return Response.ok(entityBuilder.buildAccountJson(account.get(), uriInfo)).cacheControl(cc).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Updates an account.
     */
    @PUT
    @RolesAllowed("advisor")
    @Path("{regNo}-{accountNo}")
    @Produces("application/hal+json")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdate(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
            @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
            @Valid AccountUpdate update,
            @Context UriInfo uriInfo, @Context Request request) {
        if (!regNo.equals(update.getRegNo()) || !accountNo.equals(update.getAccountNo())) {
            throw new BadInputException("Account in uri " + regNo + "-" + accountNo + " is not matching account in body "
                    + update.getRegNo() + "-" + update.getAccountNo() + "!");
        }

        Account account = admin.findAccount(regNo, accountNo).map(acc -> {
            acc.setName(update.getName());
            return acc;
        }).orElse(new Account(regNo, accountNo, update.getName()));
        admin.save(account);
        CacheControl cc = new CacheControl();
        cc.setMaxAge(60);
        return Response.ok(entityBuilder.buildAccountJson(account, uriInfo)).cacheControl(cc).build();
    }

}
