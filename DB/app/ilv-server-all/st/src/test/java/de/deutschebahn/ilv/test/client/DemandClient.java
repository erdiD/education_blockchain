package de.deutschebahn.ilv.test.client;

import de.deutschebahn.ilv.TestUtils;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

/**
 * Created by AlbertLacambraBasil on 16.06.2017.
 */
public class DemandClient extends ProjectClient {

    public void verifyUpdate(WebTarget webTarget, Response.Status expectedStatus) {
        JsonObject demand = TestUtils.getJsonObjectFromFile("update-demand.json");
        Response response = webTarget.path(getObjectResourcePath()).path(String.valueOf(getDemandId())).request().put(Entity.json(demand));
        assertThat(response.getStatus(), is(expectedStatus.getStatusCode()));
    }

    @Override
    protected String getObjectKey() {
        return "demand";
    }

    @Override
    protected boolean checkHistory() {
        return true;
    }

    @Override
    protected JsonObject getAndValidateObjectExistenceOnProjectEntity(JsonValue jsonValue) {
        JsonObject jsonObject = (JsonObject) jsonValue;
        assertThat("invalid demand received: " + jsonObject,
                jsonObject.getString("id", "-1"), is(not("-1"))
        );

        return jsonObject;
    }

    @Override
    protected String getEntityId() {
        return getDemandId();
    }

    @Override
    public String getObjectResourcePath() {
        return "demand";
    }

    @Override
    protected String assignObjectId(String id) {
        setDemandId(id);
        return getDemandId();
    }

    @Override
    protected JsonObject loadJsonFormFile() {
        return TestUtils.getJsonObjectBuilderFromFile("create-demand.json").build();
    }
}

