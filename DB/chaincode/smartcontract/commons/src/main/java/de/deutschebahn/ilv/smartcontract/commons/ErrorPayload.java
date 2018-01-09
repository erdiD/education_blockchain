package de.deutschebahn.ilv.smartcontract.commons;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 17.10.2017.
 */
public class ErrorPayload {

    private final String message;
    private final MessageStatus status;

    public ErrorPayload(String message, MessageStatus status) {
        this.message = message;
        this.status = status;
    }

    public ErrorPayload(JsonObject jsonObject) {
        message = SerializationHelper.getValueOrDefault("message", jsonObject::getString, "");
        status = SerializationHelper.getValueOrException("error", jsonObject::getString, MessageStatus::valueOf);
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("error", status.name())
                .add("message", message)
                .build();
    }

    public String getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }
}
