package de.deutschebahn.ilv;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.InputStream;

public class TestUtils {

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

    public static JsonObjectBuilder toBuilder(JsonObject object) {

        JsonObjectBuilder builder = Json.createObjectBuilder();
        object.entrySet().forEach(pair -> {
            builder.add(pair.getKey(), pair.getValue());
        });

        return builder;
    }

    public static JsonObjectBuilder getJsonObjectBuilderForProjectObjectFromFile(String filename, int demandId) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        JsonObject object = Json.createReader(is).readObject();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        object.getJsonObject("offer").entrySet().forEach(pair -> {
            builder.add(pair.getKey(), pair.getValue());
        });

        builder.add("projectID", demandId);

        return Json.createObjectBuilder().add("offer", builder);
    }

    public static int extractId(JsonObject jsonObject) {
        return jsonObject.getInt("id");
    }
}
