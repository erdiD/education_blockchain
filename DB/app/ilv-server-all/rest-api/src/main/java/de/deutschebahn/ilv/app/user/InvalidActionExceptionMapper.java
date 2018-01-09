package de.deutschebahn.ilv.app.user;

import de.deutschebahn.ilv.bussinesobject.InvalidActionException;

import javax.json.Json;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidActionExceptionMapper implements ExceptionMapper<InvalidActionException> {

    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(InvalidActionException e) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(Json.createObjectBuilder().add("error", e.getMessage()).build()).build();
    }
}
