package de.deutschebahn.ilv;


import de.deutschebahn.ilv.businessobject.BOAction;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.hamcrest.CoreMatchers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by alacambra on 02.06.17.
 */
public class TestUtils {

    private static Logger logger = Logger.getLogger(TestUtils.class.getName());

    public static JsonObject getJsonObjectFromFile(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        return Json.createReader(is).readObject();
    }

    public static JsonObjectBuilder getJsonObjectBuilderFromFile(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        JsonObject object = Json.createReader(is).readObject();

        JsonObjectBuilder builder = toBuilder(object);
        return builder;
    }

    public static Response uploadFile(WebTarget webTarget, String filePath, String filePartName) {
        File file = new File(filePath);
        final FileDataBodyPart filePart = new FileDataBodyPart(filePartName, file);
        final MultiPart multiPartEntity = new FormDataMultiPart().bodyPart(filePart);
        Response response = webTarget.request().post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));
        return response;
    }

    public static boolean JsonArrayContainsStringItem(JsonArray jsonArray, String item) {

        for (int i = 0; i < jsonArray.size(); i++) {
            if (jsonArray.getString(i).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static JsonArrayContainsStringItems containsJsonStrings(String value) {
        return new JsonArrayContainsStringItems(value);
    }

    public static JsonArrayContainsStringItems containsJsonStrings(String... value) {
        return new JsonArrayContainsStringItems(value);
    }

    public static JsonArrayContainsStringItems containsAllJsonStrings(String... value) {
        return new JsonArrayContainsStringItems(true, value);
    }

    public static JsonArrayContainsAllStates containsAllStates(BOAction... value) {
        return new JsonArrayContainsAllStates(value);
    }

    public static JsonObjectBuilder toBuilder(JsonObject object) {

        JsonObjectBuilder builder = Json.createObjectBuilder();
        object.entrySet().forEach(pair -> {
            builder.add(pair.getKey(), pair.getValue());
        });

        return builder;
    }

    public static JsonObject getJsonEntity(Response response) {

        if (!Response.Status.Family.SUCCESSFUL.equals(Response.Status.Family.familyOf(response.getStatus()))) {

            if (!MediaType.valueOf(APPLICATION_JSON).equals(response.getMediaType())) {
                return Json.createObjectBuilder().add("ConvertedResponseBody", response.readEntity(String.class)).build();
            }

            JsonObject entity = response.readEntity(JsonObject.class);
            if (entity == null) {
                return Json.createObjectBuilder().add("error",
                        "No content received from server. Error code is " + response.getStatus()).build();
            }
            return entity;
        } else if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            return Json.createObjectBuilder().add("error", "Just a default message to avoid NPE on test. Not sent from server").build();
        } else {
            JsonObject entity = response.readEntity(JsonObject.class);
            if (entity == null) {
                return Json.createObjectBuilder().add("error",
                        "No content received from server, though it should. Status code should be 204").build();
            }

            return entity;
        }
    }

    public static String extractId(JsonObject jsonObject) {
        if (!jsonObject.containsKey("id")) {
            logger.info("[extractId] id field not found. Object=" + jsonObject.toString());

        }
        return jsonObject.getString("id");
    }

    public static void loginUser(WebTarget webTarget, String username) {
        Response r = webTarget.path("session").request().header("Authorization", username).post(Entity.json(Json.createObjectBuilder().build()));
        assertThat("failed by " + username, r.getStatus(), CoreMatchers.is(200));
        assertThat(NewCookie.valueOf(r.getHeaderString("Set-Cookie")), notNullValue());
    }
}
