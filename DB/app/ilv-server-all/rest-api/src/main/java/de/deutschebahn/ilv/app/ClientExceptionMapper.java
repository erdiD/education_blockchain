package de.deutschebahn.ilv.app;

import javax.json.Json;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientExceptionMapper implements ExceptionMapper<ClientException> {

    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(ClientException e) {
        return Response.status(e.getResponse().getStatus()).entity(Json.createObjectBuilder().add("error", e.getMessage()).build()).build();
    }
}
