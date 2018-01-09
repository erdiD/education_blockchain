package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class StringMessage {
    private String value;

    public StringMessage(String value) {
        this.value = value;
    }

    public StringMessage(List<String> params) {
        this(SerializationHelper.stringToJsonObject(params.get(0)));
    }

    public StringMessage(JsonObject jsonObject) {
        value = jsonObject.getString("value");
    }

    public String getValue() {
        return value;
    }

    //TODO: change name and extract interface
    public JsonObject toJson() {
        return Json.createObjectBuilder().add("value", value).build();
    }
}
