package de.deutschebahn.ilv.smartcontract.commons;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class ChaincodeResponseMessage {

    private final MessageStatus status;
    private final JsonObject payload;
    private long mid = 0;

    public ChaincodeResponseMessage(MessageStatus status, JsonObject payload) {
        Objects.requireNonNull(status);
        Objects.requireNonNull(payload);

        this.status = status;
        this.payload = payload;
    }

    public ChaincodeResponseMessage(JsonObject jsonObject) {
        this(MessageStatus.valueOf(jsonObject.getString("status")), jsonObject.getJsonObject("payload"));
    }

    public ChaincodeResponseMessage(byte[] jsonObject) {
        this(SerializationHelper.bytesToJsonObject(jsonObject));
    }

    public ChaincodeResponseMessage(MessageStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
        this.payload = Json.createObjectBuilder().build();
    }

    public JsonObject getPayload() {
        return payload;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public byte[] asBytes() {
        return Json
                .createObjectBuilder()
                .add("status", status.name())
                .add("payload", payload)
                .build()
                .toString()
                .getBytes();
    }

    public void setMessageId(long mid) {
        if (this.mid != 0) {
            throw new RuntimeException(String.format("Mid already exist. # currentMid=%l, newMid=%l", this.mid, mid));
        }
        this.mid = mid;
    }

    public long getMid() {
        return mid;
    }

    @Override
    public String toString() {
        return "ChaincodeResponseMessage{" +
                "mid=" + mid +
                ", status=" + status +
                ", payload=" + payload +
                '}';
    }
}
