package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.*;

import javax.json.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.getValueOrException;
import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.serializeValueOrException;


/**
 * Created by alacambra on 04.06.17.
 */
public class DeliveryDataConverter implements DataConverter<Delivery> {

    //TODO injection...
    DeliveryEntryDataConverter deliveryEntryDataConverter = new DeliveryEntryDataConverter();

    @Override
    public JsonObject serialize(Delivery delivery, SerializeView view) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        serializeValueOrException(builder::add, SerializationHelper::serializeToJsonArray, "psps", delivery.getPsps());
        serializeValueOrException(builder::add, SerializationHelper::serializeBigDecimalCurrency, "budget", delivery.getBudget());
        serializeValueOrException(builder::add, Enum::name, "paymentType", delivery.getPaymentType());
        serializeValueOrException(builder::add, Enum::name, "contractType", delivery.getContractType());
        serializeValueOrException(builder::add, Enum::name, "state", delivery.getState());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "startDate", delivery.getStartDate());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "deliveryDate", delivery.getDeliveryDate());
        serializeValueOrException(builder::add, "projectId", delivery.getProjectId());

        JsonArray objects = delivery.getDeliveryEntries().stream().map(de -> deliveryEntryDataConverter.serialize(de, view))
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();

        serializeValueOrException(builder::add, "entries", objects);

        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInFabricToJsonToApp:
                serializeValueOrException(builder::add, SerializationHelper::serializeToJsonArray, "availableActions", delivery.getAvailableActions());
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectBetweenChaincodes:
                serializeValueOrException(builder::add, "id", delivery.getId());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "creationTime", delivery.getStartDate());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModified", delivery.getLastModified());
                break;
        }

        return builder.build();
    }

    @Override
    public Delivery deserialize(JsonObject jsonObject, DeserializeView view) {

        Delivery delivery = new Delivery();
        delivery.setPsps(getValueOrException("psps", jsonObject::getJsonArray, SerializationHelper::deserializeToArray));
        delivery.setBudget(getValueOrException("budget", jsonObject::getString, SerializationHelper::convertToBigDecimal));
        delivery.setPaymentType(getValueOrException("paymentType", jsonObject::getString, PaymentType::valueOf));
        delivery.setContractType(getValueOrException("contractType", jsonObject::getString, ContractType::valueOf));
        delivery.setState(getValueOrException("state", jsonObject::getString, ObjectState::valueOf));
        delivery.setStartDate(getValueOrException("startDate", jsonObject::getString, SerializationHelper::convertToDate));
        delivery.setDeliveryDate(getValueOrException("deliveryDate", jsonObject::getString, SerializationHelper::convertToDate));
        delivery.setProjectId(getValueOrException("projectId", jsonObject::getString));

        List<DeliveryEntry> deliveryEntries = Optional
                .ofNullable(jsonObject.getJsonArray("entries"))
                .orElse(Json.createArrayBuilder().build())
                .stream().map(v -> (JsonObject) v)
                .map(v -> deliveryEntryDataConverter.deserialize(v, view)).collect(Collectors.toList());

        delivery.setDeliveryEntries(deliveryEntries);

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case jsonFromFabricToObjectInApp:
                List<String> actions = getValueOrException("availableActions", jsonObject::getJsonArray, SerializationHelper::deserializeToArray);
                delivery.setAvailableActions(actions);
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case objectBetweenChaincodes:
                delivery.setId(jsonObject.getString("id"));
                delivery.setDateCreated(getValueOrException("creationTime", jsonObject::getString, SerializationHelper::convertToDate));
                delivery.setLastModified(getValueOrException("lastModified", jsonObject::getString, SerializationHelper::convertToDate));
                break;
        }

        return delivery;
    }

    @Override
    public String getAssignedType() {
        return Delivery.class.getSimpleName();
    }
}
