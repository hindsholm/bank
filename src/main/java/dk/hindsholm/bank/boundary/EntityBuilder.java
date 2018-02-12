package dk.hindsholm.bank.boundary;

import dk.hindsholm.bank.entity.Account;
import dk.hindsholm.bank.entity.Transaction;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.core.UriInfo;

public class EntityBuilder {

    private static final String EMBEDDED = "_embedded";
    private static final String HREF = "href";
    private static final String LINKS = "_links";
    private static final String SELF = "self";

    public JsonObject buildAccountsJson(List<Account> accounts, UriInfo uriInfo) {
        URI self = uriInfo.getBaseUriBuilder()
                .path(AccountResource.class)
                .build();
        return Json.createObjectBuilder()
                .add(LINKS, Json.createObjectBuilder()
                        .add(SELF, Json.createObjectBuilder()
                                .add(HREF, self.toString())))
                .add(EMBEDDED, Json.createObjectBuilder()
                        .add("accounts", buildAccountsJsonArray(accounts, uriInfo)))
                .build();
    }

    JsonValue buildAccountsJsonArray(List<Account> accounts, UriInfo uriInfo) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Account account : accounts) {
            URI self = buildAccountLink(account, uriInfo);
            URI transactions = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .build(account.getRegNo(), account.getAccountNo());
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("regNo", account.getRegNo())
                    .add("accountNo", account.getAccountNo())
                    .add("name", account.getName())
                    .add(LINKS, Json.createObjectBuilder()
                            .add(SELF, Json.createObjectBuilder()
                                    .add(HREF, self.toString()))
                            .add("account:transactions", Json.createObjectBuilder()
                                    .add(HREF, transactions.toString()))));
        }
        return arrayBuilder.build();
    }

    URI buildAccountLink(Account account, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(AccountResource.class)
                .path(AccountResource.class, "get")
                .build(account.getRegNo(), account.getAccountNo());
    }

    public JsonObject buildAccountJson(Account account, UriInfo uriInfo) {
        URI self = buildAccountLink(account, uriInfo);
        return Json.createObjectBuilder()
                .add("regNo", account.getRegNo())
                .add("accountNo", account.getAccountNo())
                .add("name", account.getName())
                .add(LINKS, Json.createObjectBuilder()
                        .add(SELF, Json.createObjectBuilder()
                                .add(HREF, self.toString())))
                .add(EMBEDDED, Json.createObjectBuilder()
                        .add("transactions", buildTransactionsJsonArray(account.getTransactions(), uriInfo)))
                .build();
    }

    public JsonObject buildTransactionsJson(Account account, UriInfo uriInfo) {
        URI self = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .build(account.getRegNo(), account.getAccountNo());
        return Json.createObjectBuilder()
                .add(LINKS, Json.createObjectBuilder()
                        .add(SELF, Json.createObjectBuilder()
                                .add(HREF, self.toString())))
                .add(EMBEDDED, Json.createObjectBuilder()
                        .add("transactions", buildTransactionsJsonArray(account.getTransactions(), uriInfo)))
                .build();
    }

    public JsonObject buildTransactionJson(Transaction transaction, UriInfo uriInfo) {
        URI self = uriInfo.getBaseUriBuilder()
                .path(TransactionResource.class)
                .path(TransactionResource.class, "get")
                .build(transaction.getAccount().getRegNo(), transaction.getAccount().getAccountNo(), transaction.getId());
        return Json.createObjectBuilder()
                .add(LINKS, Json.createObjectBuilder()
                        .add(SELF, Json.createObjectBuilder()
                                .add(HREF, self.toString())))
                .add("id", transaction.getId())
                .add("amount", transaction.getAmount())
                .add("description", transaction.getDescription())
                .build();
    }

    JsonArray buildTransactionsJsonArray(Set<Transaction> transactions, UriInfo uriInfo) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Transaction t : transactions) {
            URI self = uriInfo.getBaseUriBuilder()
                    .path(TransactionResource.class)
                    .path(TransactionResource.class, "get")
                    .build(t.getAccount().getRegNo(), t.getAccount().getAccountNo(), t.getId());
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("id", t.getId())
                    .add("amount", t.getAmount())
                    .add("description", t.getDescription())
                    .add(LINKS, Json.createObjectBuilder()
                            .add(SELF, Json.createObjectBuilder()
                                    .add(HREF, self.toString()))));

        }
        return arrayBuilder.build();
    }

}
