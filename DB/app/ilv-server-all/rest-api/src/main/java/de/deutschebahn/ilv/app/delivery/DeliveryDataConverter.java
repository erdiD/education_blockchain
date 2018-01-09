package de.deutschebahn.ilv.app.delivery;

import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryEntryError;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryParserResult;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.*;
import java.util.Collection;

public class DeliveryDataConverter {

    @Inject
    UserDataConverter userDataConverter;

    public JsonObject serialize(Delivery delivery, DeliveryParserResult result) {

        JsonObjectBuilder resultBuilder = serializeAsBuilder(delivery);
        JsonArrayBuilder successBuilder = Json.createArrayBuilder();
        JsonArrayBuilder errorsBuilder = Json.createArrayBuilder();

//        result.getDeliveryEntries().stream().map(this::serialize).forEach(successBuilder::add);
        result.getPersistedDeliveryEntries().stream().map(de -> this.serialize(de))
                .forEach(successBuilder::add);
        result.getErrors().stream().map(this::serialize).forEach(errorsBuilder::add);

        resultBuilder.add("success", successBuilder).add("errors", errorsBuilder);
        return resultBuilder.build();
    }

    public JsonObject serialize(DeliveryEntry deliveryEntry) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("id", deliveryEntry.getId())
                .add("pspId", deliveryEntry.getPspId())
                .add("paymentValue", deliveryEntry.getPaymentValue())
                .add("line", deliveryEntry.getLine())
                .add("creationDate", SerializationHelper.convertFromDate(deliveryEntry.getCreationDate()))
                .add("fileName", deliveryEntry.getFileName())
                .add("fileHash", deliveryEntry.getHash());

        if (deliveryEntry.getAchievedScope() != null) {
            builder.add("achievedScope", deliveryEntry.getAchievedScope().toString());
        }
        return builder.build();
    }

    public JsonObjectBuilder serializeAsBuilder(Delivery delivery) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("id", delivery.getId())
                .add("state", delivery.getState().name())
                .add("deliveryDate", SerializationHelper.convertFromDate(delivery.getDeliveryDate()))
                .add("messageBoardUrl", delivery.getMessageBoardUrl())
                .add("psps", serializePsps(delivery.getPsps()))
                //TODO reenable
//                .add("creator", userDataConverter.serialize(delivery.getCreator()))
//                .add("creationDate", SerializationHelper.convertFromDate(delivery.getTimestampsEntity().getDateCreated()))
//                .add("lastModified", SerializationHelper.convertFromDate(delivery.getTimestampsEntity().getLastModified()));
                ;

        return builder;
    }

    public JsonObject serialize(Delivery delivery, Collection<String> availableActions) {

        JsonObjectBuilder builder = serializeAsBuilder(delivery);
        JsonArrayBuilder entriesBuilder = Json.createArrayBuilder();
        //TODO reenable
        delivery.getDeliveryEntries().stream().map(this::serialize).forEach(entriesBuilder::add);

        return builder
                .add("entries", entriesBuilder)
                .add("availableActions", SerializationHelper.serializeToJsonArray(availableActions))
                .build();

    }

    private JsonArray serializePsps(Collection<String> psps) {
        return psps.stream().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
    }

    public JsonObject serialize(DeliveryEntryError deliveryEntryError) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("line", deliveryEntryError.getLine())
                .add("error", deliveryEntryError.getMessage());

        return builder.build();
    }


}
