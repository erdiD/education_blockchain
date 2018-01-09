package de.deutschebahn.ilv.app;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Provider
@PreMatching
@Priority(0)
public class LoggerRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    Logger logger;

    @Context
    private HttpServletRequest request;

    public void filter(ContainerRequestContext ctx) throws IOException {
        String sessionId = request.getSession() != null ? request.getSession().getId() : "none";


        String cookies = "no-cookies";
        if (request.getCookies() != null) {
            cookies = Stream.of(request.getCookies()).map(this::cookieToString).collect(Collectors.joining(" # "));
        }

        logger.info("[filter] received request:" + ctx.getMethod() + " " + ctx.getUriInfo().getAbsolutePath()
                + " || sessionId: " + sessionId
                + " || cookies: " + cookies);
    }

    private String cookieToString(Cookie cookie) {
        return cookie.getName() + ":" + cookie.getValue();
    }

    private String cookieToString(Map.Entry<String, NewCookie> cookieEntry) {
        return cookieEntry.getKey() + ":" + cookieEntry.getValue();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String sessionId = request.getSession() != null ? request.getSession().getId() : "none";
        String responseBody = Optional.ofNullable(responseContext.getEntity()).orElse("no-response-entity-available").toString();
        String cookies = "no-cookies";
        if (responseContext.getCookies() != null) {
            cookies = responseContext.getCookies().entrySet()
                    .stream()
                    .map(this::cookieToString)
                    .collect(Collectors.joining(" # "));
        }

//        responseContext.setEntity(responseBody.replace(IdUtils.SEPARATOR, "##"));
        logger.info("[filter] Sending response body: " + responseContext.getStatus() + " " + responseBody
                + " || sessionId:" + sessionId
                + " || cookies:" + cookies
        );
    }
}