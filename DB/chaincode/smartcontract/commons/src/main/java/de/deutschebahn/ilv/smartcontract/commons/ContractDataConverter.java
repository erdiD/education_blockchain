package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.ContractType;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.PaymentType;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.util.List;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.getValueOrException;
import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.serializeValueOrException;


/**
 * Created by alacambra on 04.06.17.
 */
public class ContractDataConverter implements DataConverter<Contract> {
    @Override
    public JsonObject serialize(Contract object, SerializeView view) {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        SerializationHelper.serializeValueOrException(builder::add, ObjectState::name, "state", object.getState());
        SerializationHelper.serializeValueOrException(builder::add, "projectId", object.getProjectId());
        SerializationHelper.serializeValueOrException(builder::add, "offerId", object.getOfferId());
        SerializationHelper.serializeValueOrException(builder::add, BigDecimal::toString, "budget", object.getBudget());
        SerializationHelper.serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "deliveryDate", object.getDeliveryDate());
        SerializationHelper.serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "startDate", object.getStartDate());
        SerializationHelper.serializeValueOrException(builder::add, ContractType::name, "contractType", object.getContractType());
        SerializationHelper.serializeValueOrException(builder::add, PaymentType::name, "paymentType", object.getPaymentType());

        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInFabricToJsonToApp:
                serializeValueOrException(builder::add, SerializationHelper::serializeToJsonArray, "availableActions", object.getAvailableActions());
            case objectInFabricToJsonInDatabase:
            case objectInAppToJsonToFabric:
            case objectBetweenChaincodes:
                SerializationHelper.serializeValueOrException(builder::add, "id", object.getId());
                SerializationHelper.serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "dateCreated", object.getDateCreated());
                SerializationHelper.serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModified", object.getLastModified());
                break;
        }

        return builder.build();
    }

    @Override
    public Contract deserialize(JsonObject demandJson, DeserializeView view) {
        Contract contract = new Contract();
        contract.setProjectId(SerializationHelper.getValueOrException("projectId", demandJson::getString));
        contract.setState(SerializationHelper.getValueOrException("state", demandJson::getString, ObjectState::valueOf));
        contract.setOfferId(SerializationHelper.getValueOrException("offerId", demandJson::getString));
        contract.setBudget(SerializationHelper.getValueOrException("budget", demandJson::getString, SerializationHelper::convertToBigDecimal));
        contract.setStartDateDate(SerializationHelper.getValueOrException("startDate", demandJson::getString, SerializationHelper::convertToDate));
        contract.setContractType(SerializationHelper.getValueOrException("contractType", demandJson::getString, ContractType::valueOf));
        contract.setPaymentType(SerializationHelper.getValueOrException("paymentType", demandJson::getString, PaymentType::valueOf));
        contract.setDeliveryDate(SerializationHelper.getValueOrException("deliveryDate", demandJson::getString, SerializationHelper::convertToDate));

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case jsonFromFabricToObjectInApp:
                List<String> actions = getValueOrException("availableActions", demandJson::getJsonArray, SerializationHelper::deserializeToArray);
                contract.setAvailableActions(actions);
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case objectBetweenChaincodes:
                contract.setId(SerializationHelper.getValueOrException("id", demandJson::getString));
                contract.setDateCreated(SerializationHelper.getValueOrException("dateCreated", demandJson::getString, SerializationHelper::convertToDate));
                contract.setLastModified(SerializationHelper.getValueOrException("lastModified", demandJson::getString, SerializationHelper::convertToDate));
                break;
        }

        return contract;
    }

    @Override
    public String getAssignedType() {
        return Contract.class.getSimpleName();
    }
}
