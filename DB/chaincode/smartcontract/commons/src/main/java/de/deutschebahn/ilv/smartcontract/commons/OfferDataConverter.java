package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.*;

import javax.json.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.*;


public class OfferDataConverter implements DataConverter<Offer> {

    //TODO: injection
    final AttachmentDataConverter attachmentDataConverter = new AttachmentDataConverter();

    @Override
    public JsonObject serialize(Offer offer, SerializeView view) {

        JsonObjectBuilder builder = Json.createObjectBuilder();
        serializeValueOrException(builder::add, "projectId", offer.getProjectId());
        serializeValueOrException(builder::add, SerializationHelper::serializeBigDecimalCurrency, "price", offer.getPrice());
        serializeValueIfPresent(builder::add, "description", offer.getDescription());
        serializeValueIfPresent(builder::add, "messageBoardUrl", offer.getMessageBoardUrl());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "deliveryDate", offer.getDeliveryDate());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "startDate", offer.getStartDate());
        serializeValueOrException(builder::add, Enum::name, "paymentType", offer.getPaymentType());
        serializeValueOrException(builder::add, Enum::name, "contractType", offer.getContractType());


        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInFabricToJsonToApp:
                List<AttachmentEntity> attachmentEntities = offer.getAttachmentEntities();
                JsonArray jsonArray = attachmentEntities.stream().map(att -> attachmentDataConverter.serialize(att, view))
                        .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
                serializeValueOrException(builder::add, "attachments", jsonArray);
                serializeValueOrException(builder::add, SerializationHelper::serializeToJsonArray, "availableActions", offer.getAvailableActions());
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectBetweenChaincodes:
                serializeValueOrException(builder::add, "id", offer.getId());
                serializeValueOrException(builder::add, "organizationId", offer.getOrganizationId());
                serializeValueOrException(builder::add, Enum::name, "state", offer.getState());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "creationTime", offer.getDateCreated());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModifiedTime", offer.getLastModified());
                serializeValueOrException(builder::add, "creatorId", offer.getCreatorId());
                break;
        }


        return builder.build();
    }

    public Offer deserialize(JsonObject jsonOffer, DeserializeView view) {

        Offer offer = new Offer();
        offer.setProjectId(getValueOrException("projectId", jsonOffer::getString));
        offer.setDescription(jsonOffer.getString("description", ""));
        offer.setPrice(getValueOrException("price", jsonOffer::getString, SerializationHelper::convertToBigDecimal));
        offer.setMessageBoardUrl(jsonOffer.getString("messageBoardUrl", ""));
        offer.setPaymentType(getValueOrException(jsonOffer::getString, "paymentType", PaymentType.class));
        offer.setDeliveryDate(getValueOrException("deliveryDate", jsonOffer::getString, SerializationHelper::convertToDate));
        offer.setStartDate(getValueOrException("startDate", jsonOffer::getString, SerializationHelper::convertToDate));
        offer.setContractType(getValueOrException(jsonOffer::getString, "contractType", ContractType.class));
        offer.setProjectId(getValueOrException("projectId", jsonOffer::getString));

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case jsonFromFabricToObjectInApp:
                JsonArray jsonArray = getValueOrException("attachments", jsonOffer::getJsonArray);
                List<AttachmentEntity> attachments = jsonArray.stream()
                        .map(v -> (JsonObject) v)
                        .map(jAtt -> attachmentDataConverter.deserialize(jAtt, view))
                        .collect(Collectors.toList());
                offer.setAttachmentEntities(new ArrayList<>(attachments));
                List<String> actions = getValueOrException("availableActions", jsonOffer::getJsonArray, SerializationHelper::deserializeToArray);
                offer.setAvailableActions(actions);
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case objectBetweenChaincodes:
                offer.setId(getValueOrException("id", jsonOffer::getString));
                offer.setOrganizationId(getValueOrException("organizationId", jsonOffer::getString));
                offer.setState(getValueOrException(jsonOffer::getString, "state", ObjectState.class));
                offer.setDateCreated(getValueOrDefault("creationTime", jsonOffer::getString, SerializationHelper::convertToDate, new Date()));
                offer.setLastModified(getValueOrDefault("lastModifiedTime", jsonOffer::getString, SerializationHelper::convertToDate, new Date()));
                offer.setCreatorId(getValueOrException("creatorId", jsonOffer::getString));
                break;
        }
        return offer;
    }

    @Override
    public String getAssignedType() {
        return Offer.class.getSimpleName();
    }

    public JsonObject serialize(Offer offer) {
        return serialize(offer, SerializeView.objectInAppToJsonToFabric);
    }

    public JsonArray serialize(List<Offer> offers) {

        if (offers == null || offers.isEmpty()) {
            return Json.createArrayBuilder().build();
        }

        return offers
                .stream()
                .map(this::serialize)
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();
    }
}
