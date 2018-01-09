package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.*;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
public class StringList {
    Collection<String> values;

    public StringList(Collection<String> values) {
        this.values = values;
    }

    public StringList(JsonObject jsonObject) {
        this.values = jsonObject.getJsonArray("values")
                .stream()
                .map(v -> (JsonString) v)
                .map(JsonString::getString)
                .collect(Collectors.toList());
    }

    public StringList(String string) {
        this(SerializationHelper.stringToJsonObject(string));
    }

    public JsonObject toJson() {
        JsonArray jsonArray = values.stream()
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();

        return Json.createObjectBuilder().add("values", jsonArray).build();
    }

    public Collection<String> getValues() {
        return values;
    }
}
