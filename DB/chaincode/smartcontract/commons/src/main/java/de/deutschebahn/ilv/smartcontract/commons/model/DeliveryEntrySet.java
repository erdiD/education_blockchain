package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import de.deutschebahn.ilv.smartcontract.commons.AttachmentDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DeliveryEntryDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.*;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Created by AlbertLacambraBasil on 21.10.2017.
 */
public class DeliveryEntrySet {
    private DataConverter.SerializeView serializeView;
    private AttachmentEntity attachmentEntity;
    private List<DeliveryEntry> deliveryEntries;
    private final AttachmentDataConverter attachmentDataConverter;
    private final DeliveryEntryDataConverter deliveryEntryDataConverter;

    public DeliveryEntrySet(
            AttachmentEntity attachmentEntity,
            List<DeliveryEntry> deliveryEntries,
            AttachmentDataConverter attachmentDataConverter,
            DeliveryEntryDataConverter deliveryEntryDataConverter,
            DataConverter.SerializeView serializeView
    ) {
        this.serializeView = serializeView;
        Objects.requireNonNull(deliveryEntries);
        Objects.requireNonNull(attachmentEntity);
        this.attachmentEntity = attachmentEntity;
        this.deliveryEntries = deliveryEntries;
        this.attachmentDataConverter = attachmentDataConverter;
        this.deliveryEntryDataConverter = deliveryEntryDataConverter;
    }

    public DeliveryEntrySet(String payload, AttachmentDataConverter attachmentDataConverter, DeliveryEntryDataConverter deliveryEntryDataConverter, DataConverter.DeserializeView view) {
        this(SerializationHelper.stringToJsonObject(payload), attachmentDataConverter, deliveryEntryDataConverter, view);
    }

    public DeliveryEntrySet(JsonObject jsonObject, AttachmentDataConverter attachmentDataConverter, DeliveryEntryDataConverter deliveryEntryDataConverter, DataConverter.DeserializeView view) {
        Objects.requireNonNull(attachmentDataConverter);
        Objects.requireNonNull(deliveryEntryDataConverter);
        Objects.requireNonNull(jsonObject);

        this.attachmentDataConverter = attachmentDataConverter;
        this.deliveryEntryDataConverter = deliveryEntryDataConverter;

        JsonObject attJson = jsonObject.getJsonObject("attachmentEntity");
        JsonArray entriesJson = jsonObject.getJsonArray("deliveryEntries");

        attachmentEntity = attachmentDataConverter.deserialize(attJson, view);
        deliveryEntries = entriesJson.stream()
                .map(v -> (JsonObject) v)
                .map(v -> deliveryEntryDataConverter.deserialize(v, view))
                .collect(toList());
    }

    public JsonObject toJson() {
        JsonObject attJson = attachmentDataConverter.serialize(attachmentEntity, serializeView);
        JsonArrayBuilder entriesJson = deliveryEntries
                .stream()
                .map(entry -> deliveryEntryDataConverter.serialize(entry, serializeView))
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add);

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("attachmentEntity", attJson)
                .add("deliveryEntries", entriesJson);

        return builder.build();
    }

    public AttachmentEntity getAttachmentEntity() {
        return attachmentEntity;
    }

    public List<DeliveryEntry> getDeliveryEntries() {
        return deliveryEntries;
    }
}