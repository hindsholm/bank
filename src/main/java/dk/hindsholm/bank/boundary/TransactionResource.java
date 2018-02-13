package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.control.AccountAdministration;
import dk.hindsholm.bank.entity.Account;
import dk.hindsholm.bank.entity.Transaction;
import java.util.Optional;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/accounts/{regNo}-{accountNo}/transactions")
public class TransactionResource {

    @EJB
    private AccountAdministration admin;

    @Inject
    EntityBuilder entityBuilder;

    /**
     * Lists all transactions for the given account.
     */
    @GET
    @Produces({"application/hal+json;v=1", "application/hal+json"})
    public Response list(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo,
            @Context UriInfo uriInfo, @Context Request request) {
        Optional<Account> account = admin.findAccount(regNo, accountNo);
        if (account.isPresent()) {
            CacheControl cc = new CacheControl();
            cc.setMaxAge(10);
            return Response.ok(entityBuilder.buildTransactionsJson(account.get(), uriInfo)).cacheControl(cc).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * GETs a specific transaction.
     */
    @GET
    @Path("{id}")
    @Produces({"application/hal+json;v=1", "application/hal+json"})
    public Response get(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo, @PathParam("id") String id,
            @Context UriInfo uriInfo, @Context Request request) {
        Optional<Transaction> transaction = admin.findTransaction(regNo, accountNo, id);
        if (transaction.isPresent()) {
            CacheControl cc = new CacheControl();
            cc.setMaxAge(24 * 60 * 60);
            return Response.ok(entityBuilder.buildTransactionJson(transaction.get(), uriInfo)).cacheControl(cc).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
