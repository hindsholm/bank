package dk.hindsholm.bank.boundary;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

// See http://www.adam-bien.com/roller/abien/entry/microservices_simplify_exception_handling_in
@ApplicationException(rollback = false)
public class BadInputException extends WebApplicationException {

    public BadInputException(String message) {
        super(message, Status.BAD_REQUEST);
    }

}
