package de.deutschebahn.ilv.app.user;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.bussinesobject.UserServiceFacade;
import de.deutschebahn.ilv.domain.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;

/**
 * Created by alacambra on 02.06.17.
 */

@Path("session")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class SessionResource implements Serializable {

    @Inject
    UserDataConverter dataConverter;

    @Inject
    UserServiceFacade userServiceFacade;

    @Context
    private HttpServletRequest request;

    @Inject
    private LoggedUser loggedUser;

    public static final String CURRENT_USER_KEY = "currentUser";

    @POST
    public Response login(@HeaderParam("Authorization") String username) {

        if (username == null) {
            throw ClientException.createMissingHeader("Authorization");
        }

        User user = userServiceFacade.getById(username + "_id")
                .orElseThrow(() -> ClientException.createNotFoundError(username, User.class));

        loggedUser.setUser(user);
        return Response.ok().entity(dataConverter.serialize(user)).build();
    }

    @GET
    public Response getLoggedUser() {
        return Response.ok().entity(dataConverter.serialize(loggedUser.getUser())).build();
    }

    @GET
    @Path("auto")
    public Response autoLogin() {
        return login("christian");
    }

    @DELETE
    public void deleteSession() {
        HttpSession httpSession = request.getSession(false);
        httpSession.invalidate();
    }


}
