package de.deutschebahn.ilv;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Provider
public class RequestResponseFilter implements ClientRequestFilter, ClientResponseFilter {

    static AtomicLong counter = new AtomicLong(0);
    NewCookie sessionCookie;

    static final Logger logger = Logger.getLogger(RequestResponseFilter.class.getName());

    public RequestResponseFilter() {
        logger.info("New filter instance");
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.fine("Sending request:" + counter.get());
        if (sessionCookie != null) {
            logger.fine("Adding session cookie=" + sessionCookie);
            requestContext.getHeaders().put("Cookie", Arrays.asList(sessionCookie));
        }

    }

    public void filter(ClientRequestContext requestContext,
                       ClientResponseContext responseContext) throws IOException {
        logger.fine("Receiving response:" + counter.getAndIncrement());

        if (responseContext.getCookies().get("JSESSIONID") != null) {
            sessionCookie = responseContext.getCookies().get("JSESSIONID");
            logger.fine("Received sessionCookie=" + sessionCookie);
        }

        if (responseContext.getStatus() == 404) {
            logger.info("[filter] URL not found: " + requestToString(requestContext));
        }

    }

    private String requestToString(ClientRequestContext context) {
        StringBuilder builder = new StringBuilder();
        builder.append(context.getMethod())
                .append("  ")
                .append(context.getMediaType())
                .append("  ")
                .append(context.getUri().toString());

        return builder.toString();
    }
}