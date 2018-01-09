package de.deutschebahn.ilv.test.client;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by AlbertLacambraBasil on 22.08.2017.
 */
public class StatsClient {
    public JsonObject getStats(WebTarget webTarget, String demandId){
        Response response = webTarget.path("stats").path(String.valueOf(demandId)).request().get();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        return response.readEntity(JsonObject.class);
    }
}
