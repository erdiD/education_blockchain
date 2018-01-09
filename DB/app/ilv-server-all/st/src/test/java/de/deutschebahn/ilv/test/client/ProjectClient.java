package de.deutschebahn.ilv.test.client;

import de.deutschebahn.ilv.TestUtils;
import de.deutschebahn.ilv.businessobject.BOAction;
import de.deutschebahn.ilv.businessobject.BOState;
import org.hamcrest.Matchers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by AlbertLacambraBasil on 16.06.2017.
 */
public abstract class ProjectClient {
    private static final Logger logger = Logger.getLogger(ProjectClient.class.getName());
    private String demandId;
    private boolean forceHistoryCheck = false;
    static boolean validationEnabled = true;

    protected abstract String getObjectKey();

    public void setDemandId(String demandId) {
        this.demandId = demandId;
    }

    protected abstract boolean checkHistory();

    protected abstract JsonObject getAndValidateObjectExistenceOnProjectEntity(JsonValue jsonValue);

    protected abstract String getEntityId();

    public abstract String getObjectResourcePath();

    public String create(WebTarget webTarget) {
        JsonObject object = loadJsonFormFile();

        Response r = webTarget.path(getObjectResourcePath()).request().post(Entity.json(object));
        assertThat(r.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        return assignObjectId(TestUtils.extractId(r.readEntity(JsonObject.class)));
    }

    protected abstract String assignObjectId(String id);

    public void createExpectErrorStatus(WebTarget webTarget, Response.Status status) {
        JsonObject object = loadJsonFormFile();

        Response r = webTarget.path(getObjectResourcePath()).request().post(Entity.json(object));
        assertThat(r.getStatus(), is(status.getStatusCode()));
    }

    protected abstract JsonObject loadJsonFormFile();

    public void validateState(WebTarget webTarget, String expectedState, BOAction... expectedAvailableActions) {
        long start = System.currentTimeMillis();
        logger.info(String.format("[validateState] expectedState=%s, Actions=%s", expectedState, Stream.of(expectedAvailableActions).map(Enum::name).collect(Collectors.joining(", "))));

        JsonObject entity = get(webTarget, Response.Status.OK);
        assertThat(entity.getString("state", "none"), is(expectedState));

        if (forceHistoryCheck && checkHistory()) {
            JsonArray history = entity.getJsonArray("history");
            if (history == null) {
                history = Json.createArrayBuilder().build();
            }
            assertThat("No entries found:" + entity.toString(), history.size(), Matchers.greaterThan(0));
        }

        JsonArray availableActions = entity.getJsonArray("availableActions");
        assertThat(availableActions.toString(), availableActions, TestUtils.containsAllStates(expectedAvailableActions));
        long total = System.currentTimeMillis() - start;
        logger.info("[validateState] Validation took " + total + " millis");
    }

    public void validateState(WebTarget webTarget, BOState expectedState, BOAction... expectedAvailableActions) {
        if (validationEnabled) {
            validateState(webTarget, expectedState.name(), expectedAvailableActions);
        } else {
            logger.info("[validateState] Validation is disabled.");
        }
    }


    public JsonObject validate(WebTarget webTarget, Response.Status expectedStatus) {
        if (validationEnabled) {
            return get(webTarget, expectedStatus);
        } else {
            logger.info("[validate] Validation is disabled.");
            return null;
        }
    }

    public JsonObject get(WebTarget webTarget, Response.Status expectedStatus) {
        Response r = webTarget.path("project").path("{id}").resolveTemplate("id", demandId).request().get();
        JsonObject jsonObject = TestUtils.getJsonEntity(r);
        assertThat(jsonObject.toString(), r.getStatus(), is(expectedStatus.getStatusCode()));

        if (r.getStatus() < 400) {
            assertThat(
                    "Received project does not have required key: " + getObjectKey() + ". Response is " + jsonObject,
                    jsonObject.containsKey(getObjectKey()), is(true)
            );
            JsonValue jsonValue = jsonObject.get(getObjectKey());
            jsonObject = getAndValidateObjectExistenceOnProjectEntity(jsonValue);
        }

        return jsonObject;
    }

    public void updateState(WebTarget webTarget, BOAction action) {
        logger.info(String.format("[updateState] ObjectType=%s, id=%s, action=%s", getObjectKey(), getEntityId(), action.name()));
        Response r = webTarget.path(getObjectResourcePath()).path("state").path("{id}")
                .resolveTemplate("id", getEntityId())
                .queryParam("action", action.name())
                .request()
                .put(Entity.json(Json.createObjectBuilder().build()));

        JsonObject entity = TestUtils.getJsonEntity(r);
        assertThat(entity.toString(),
                r.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    public void updateStateExpectError(WebTarget webTarget, BOAction action) {
        if (validationEnabled) {
            Response r = webTarget.path(getObjectResourcePath()).path("state").path("{id}")
                    .resolveTemplate("id", getEntityId())
                    .queryParam("action", action.name())
                    .request()
                    .put(Entity.json(Json.createObjectBuilder().build()));

            JsonObject entity = TestUtils.getJsonEntity(r);
            assertThat(entity.toString(),
                    r.getStatus(),
                    is(Response.Status.NOT_ACCEPTABLE.getStatusCode())
            );
        } else {
            logger.info("[updateStateExpectError] Validation is disabled.");
        }
    }

    public String getDemandId() {
        return demandId;
    }


    public static void setValidationEnabled(boolean validationEnabled) {
        ProjectClient.validationEnabled = validationEnabled;
    }
}
