package de.deutschebahn.ilv.app;

import de.deutschebahn.ilv.app.user.LoggedUser;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@PreMatching
@Priority(1)
public class AuthorizationRequestFilter implements ContainerRequestFilter {

    @Inject
    Logger logger;

    @Context
    private HttpServletRequest request;

    @Inject
    LoggedUser loggedUser;

    public void filter(ContainerRequestContext ctx) throws IOException {

        HttpSession session = request.getSession(false);

//        if (!loggedUser.isLogged() && session != null) {
//            session.invalidate();
//            session = null;
//        }

        if (!("POST".equals(ctx.getMethod())
                && ctx.getUriInfo().getPath().contains("session"))
                && !ctx.getUriInfo().getPath().contains("monitor")
                && !ctx.getUriInfo().getPath().contains("auto")) {
            if (!loggedUser.isLogged()) {
                logger.info("[filter] Resource not authorized without login");
                throw ClientException.createNotAuthorized();
            } else {
                logger.fine("received request:" + ctx.getMethod() + " " + ctx.getUriInfo().getAbsolutePath() + " || " + request.getSession().getId());
            }
        }
    }
}