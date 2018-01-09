package de.deutschebahn.ilv.app;

import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InternalExceptionMapper<E extends Exception> implements ExceptionMapper<E> {
    @Inject
    Logger logger;

    @Override
    public Response toResponse(E e) {
        logger.log(Level.SEVERE, "[toResponse] error:", e);

        String message = e.getMessage();
        if (message == null) {
            message = "no message given for this exception";
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Json.createObjectBuilder().add("error", message).build()).build();
    }
}
