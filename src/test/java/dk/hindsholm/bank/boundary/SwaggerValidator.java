package dk.hindsholm.bank.boundary;

import com.atlassian.oai.validator.SwaggerRequestResponseValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.ValidationReport;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertFalse;

public class SwaggerValidator {

    SwaggerRequestResponseValidator validator;

    public SwaggerValidator(SwaggerRequestResponseValidator validator) {
        this.validator = validator;
    }

    public void validate(Request.Method method, String url, javax.ws.rs.core.Response response) {
        ValidationReport report = validator.validateResponse(url, method, new JaxRsResponse(response));
        assertFalse(report.getMessages().toString(), report.hasErrors());
    }

    private static class JaxRsResponse implements com.atlassian.oai.validator.model.Response {

        javax.ws.rs.core.Response delegate;

        public JaxRsResponse(javax.ws.rs.core.Response delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getStatus() {
            return delegate.getStatus();
        }

        @Override
        public Optional<String> getBody() {
            String body = delegate.hasEntity() ? delegate.getEntity().toString() : null;
            return Optional.ofNullable(body);
        }

        @Override
        public Collection<String> getHeaderValues(String name) {
            List<String> values = delegate.getStringHeaders().get(name);
            return values != null ? values : Collections.EMPTY_LIST;
        }

    }

}
