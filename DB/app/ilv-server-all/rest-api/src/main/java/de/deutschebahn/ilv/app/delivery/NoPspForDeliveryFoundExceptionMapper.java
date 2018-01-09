package de.deutschebahn.ilv.app.delivery;

import de.deutschebahn.ilv.bussinesobject.delivery.NoPspForDeliveryFoundException;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoPspForDeliveryFoundExceptionMapper implements ExceptionMapper<NoPspForDeliveryFoundException> {
    @Override
    public Response toResponse(NoPspForDeliveryFoundException e) {
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(Json.createObjectBuilder()
                        .add("error", e.getMessage())
                        .build()
                ).build();
    }
}
