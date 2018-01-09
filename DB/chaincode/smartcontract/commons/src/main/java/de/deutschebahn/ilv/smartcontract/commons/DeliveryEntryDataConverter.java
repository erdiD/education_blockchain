package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.DeliveryEntry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.getValueOrException;
import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.serializeValueOrException;

/**
 * Created by AlbertLacambraBasil on 21.10.2017.
 */
public class DeliveryEntryDataConverter implements DataConverter<DeliveryEntry> {

    @Override
    public JsonObject serialize(DeliveryEntry deliveryEntry, SerializeView view) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        serializeValueOrException(builder::add, "deliveryId", deliveryEntry.getDeliveryId());
        serializeValueOrException(builder::add, "userDelivererId", deliveryEntry.getUserDelivererId());
        serializeValueOrException(builder::add, "line", deliveryEntry.getLine());
        serializeValueOrException(builder::add, "pspId", deliveryEntry.getPspId());
        //TODO: Recheck conversion to bigdecimal. Specific method needed
        serializeValueOrException(builder::add, SerializationHelper::serializeBigDecimalCurrency, "achievedScope", deliveryEntry.getAchievedScope());
        serializeValueOrException(builder::add, SerializationHelper::serializeBigDecimalCurrency, "paymentValue", deliveryEntry.getPaymentValue());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "creationDate", deliveryEntry.getCreationDate());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "deliveryDate", deliveryEntry.getDeliveryDate());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "dateCreated", deliveryEntry.getDateCreated());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModified", deliveryEntry.getLastModified());

        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectInFabricToJsonToApp:
            case objectBetweenChaincodes:
                serializeValueOrException(builder::add, "id", deliveryEntry.getId());
                serializeValueOrException(builder::add, "attachmentEntityId", deliveryEntry.getAttachmentEntityId());
                serializeValueOrException(builder::add, "fileName", deliveryEntry.getFileName());
                serializeValueOrException(builder::add, "hash", deliveryEntry.getHash());
                break;
        }

        return builder.build();
    }

    @Override
    public DeliveryEntry deserialize(JsonObject jsonObject, DeserializeView view) {

        DeliveryEntry deliveryEntry = new DeliveryEntry();
        deliveryEntry.setDeliveryId(getValueOrException("deliveryId", jsonObject::getString));
        deliveryEntry.setUserDelivererId(getValueOrException("userDelivererId", jsonObject::getString));
        deliveryEntry.setLine(getValueOrException("line", jsonObject::getString));
        deliveryEntry.setPspId(getValueOrException("pspId", jsonObject::getString));
        deliveryEntry.setAchievedScope(getValueOrException("achievedScope", jsonObject::getString, SerializationHelper::convertToBigDecimal));
        deliveryEntry.setPaymentValue(getValueOrException("paymentValue", jsonObject::getString, SerializationHelper::convertToBigDecimal));
        deliveryEntry.setCreationDate(getValueOrException("creationDate", jsonObject::getString, SerializationHelper::convertToDate));
        deliveryEntry.setDeliveryDate(getValueOrException("deliveryDate", jsonObject::getString, SerializationHelper::convertToDate));
        deliveryEntry.setDateCreated(getValueOrException("dateCreated", jsonObject::getString, SerializationHelper::convertToDate));
        deliveryEntry.setLastModified(getValueOrException("lastModified", jsonObject::getString, SerializationHelper::convertToDate));

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case jsonFromFabricToObjectInApp:
            case objectBetweenChaincodes:
                deliveryEntry.setId(getValueOrException("id", jsonObject::getString));
                deliveryEntry.setAttachmentEntityId(getValueOrException("attachmentEntityId", jsonObject::getString));
                deliveryEntry.setFileName(getValueOrException("fileName", jsonObject::getString));
                deliveryEntry.setHash(getValueOrException("hash", jsonObject::getString));
                break;
        }

        return deliveryEntry;
    }


    @Override
    public String getAssignedType() {
        return DeliveryEntry.class.getSimpleName();
    }
}
