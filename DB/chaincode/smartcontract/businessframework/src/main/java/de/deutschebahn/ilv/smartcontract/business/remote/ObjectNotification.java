package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
public class ObjectNotification {
    private String objectType;
    private JsonObject object;

    public ObjectNotification(String objectType, JsonObject object) {
        this.objectType = objectType;
        this.object = object;
    }

    public ObjectNotification(JsonObject jsonObject) {
        object = SerializationHelper.getValueOrException("object", jsonObject::getJsonObject);
        objectType = SerializationHelper.getValueOrException("objectType", jsonObject::getString);
    }

    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add("objectType", objectType)
                .add("object", object)
                .build();
    }

    public String getObjectType() {
        return objectType;
    }

    public JsonObject getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "ObjectNotification{" +
                "objectType='" + objectType + '\'' +
                ", object=" + object +
                '}';
    }
}
