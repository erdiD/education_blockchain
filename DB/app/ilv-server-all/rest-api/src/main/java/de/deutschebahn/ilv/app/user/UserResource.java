package de.deutschebahn.ilv.app.user;

import de.deutschebahn.ilv.domain.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by alacambra on 02.06.17.
 */

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserDataConverter dataConverter;

    @Inject
    Logger logger;

    @GET
    public Response getAllUsers() {

        List<User> users = em.createNamedQuery(User.selectAll, User.class).getResultList();

        JsonArray arr = users.stream()
                .map(dataConverter::serialize)
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();

        return Response.ok(arr.toString()).build();
    }
}
