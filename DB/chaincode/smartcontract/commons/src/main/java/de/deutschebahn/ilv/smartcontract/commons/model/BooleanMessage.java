package de.deutschebahn.ilv.smartcontract.commons.model;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class BooleanMessage {
    private boolean value;

    public BooleanMessage(boolean value) {
        this.value = value;
    }

    public BooleanMessage(JsonObject jsonObject) {
        value = jsonObject.getBoolean("value");
    }

    public boolean getValue() {
        return value;
    }

    //TODO: change name and extract interface
    public JsonObject toJson() {
        return Json.createObjectBuilder().add("value", value).build();
    }
}
