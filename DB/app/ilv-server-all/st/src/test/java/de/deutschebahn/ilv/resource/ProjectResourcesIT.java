package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.TestUtils;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created by alacambra on 02.06.17.
 */
public class ProjectResourcesIT extends ILVScenarioIT {

    @Test
    public void getProjects() {

        JsonObject demand = TestUtils.getJsonObjectFromFile("create-demand.json");
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand").request().post(Entity.json(demand));
        JsonObject entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        String id = TestUtils.extractId(entity);

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project").request().get();
        entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(entity.toString(), entity, CoreMatchers.notNullValue());
        JsonArray projects = entity.getJsonArray("projects");
        assertThat(projects.size(), not(0));
        assertThat(fetchProject(projects, id).getString("state"), is("OPENED"));

        r = getWebTarget(MarketRoleName.OFFER_CREATOR).path("project").request().get();
        entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(entity.toString(), entity, CoreMatchers.notNullValue());
        projects = entity.getJsonArray("projects");
        assertThat(fetchProject(projects, id), nullValue());

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand/state/{id}").resolveTemplate("id", id).queryParam("action", "SUBMIT_DEMAND").request().put(Entity.json(Json.createObjectBuilder().build()));
        entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        r = getWebTarget(MarketRoleName.OFFER_CREATOR).path("project").request().get();

        entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(entity.toString(), entity, CoreMatchers.notNullValue());
        projects = entity.getJsonArray("projects");
        JsonObject project = fetchProject(projects, id);
        assertThat(project, notNullValue());
        assertThat(project.getString("budget"), notNullValue());
        assertThat(project.getJsonNumber("lastModified"), notNullValue());
    }

    public JsonObject fetchProject(JsonArray arr, String id) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.getJsonObject(i).getString("id") == id) {
                return arr.getJsonObject(i);
            }
        }

        return null;
    }
}
