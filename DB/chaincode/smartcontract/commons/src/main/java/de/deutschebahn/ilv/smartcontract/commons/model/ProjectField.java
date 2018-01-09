package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class ProjectField {
    private String fieldName;
    private String value;

    public ProjectField(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public ProjectField(JsonObject jsonObject) {
        fieldName = jsonObject.getString("fieldName");
        value = jsonObject.getString("value");
    }

    public ProjectField(List<String> params){
        this(SerializationHelper.stringToJsonObject(params.get(0)));
    }

    public ProjectField(String payload) {
        this(SerializationHelper.stringToJsonObject(payload));
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getValueAsInt() {
        return Integer.parseInt(fieldName);
    }

    public String getValue() {
        return value;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("fieldName", fieldName)
                .add("value", value)
                .build();
    }
}
