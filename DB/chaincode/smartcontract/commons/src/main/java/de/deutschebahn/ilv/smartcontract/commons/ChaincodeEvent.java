package de.deutschebahn.ilv.smartcontract.commons;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 16.10.2017.
 */
public class ChaincodeEvent {
    String name;
    JsonObject payload;

    public ChaincodeEvent(String name, JsonObject payload) {
        this.name = name;
        this.payload = payload;
    }

    public ChaincodeEvent(JsonObject jsonObject) {
        name = jsonObject.getString("name");
        payload = jsonObject.getJsonObject("payload");
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder().add("name", name).add("payload", payload).build();
    }

    public String getName() {
        return name;
    }

    public JsonObject getPayload() {
        return payload;
    }
}
