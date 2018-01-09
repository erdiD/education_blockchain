package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.Priority;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.*;


/**
 * Created by alacambra on 04.06.17.
 */
public class DemandDataConverter implements DataConverter<Demand> {

    //TODO: injection
    AttachmentDataConverter attachmentDataConverter = new AttachmentDataConverter();

    public JsonObject serialize(Demand demand, SerializeView view) {

        JsonObjectBuilder builder = basicSerialize(demand);
        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInFabricToJsonToApp:
                List<AttachmentEntity> attachmentEntities = demand.getAttachmentEntities();
                JsonArray jsonArray = attachmentEntities.stream().map(att -> attachmentDataConverter.serialize(att, view))
                        .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
                serializeValueOrException(builder::add, "attachments", jsonArray);
                serializeValueOrException(builder::add, SerializationHelper::serializeToJsonArray, "availableActions", demand.getAvailableActions());
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectBetweenChaincodes:
                serializeValueOrException(builder::add, "id", demand.getId());
                serializeValueOrException(builder::add, "creatorId", demand.getCreatorId());
                serializeValueOrException(builder::add, "organizationId", demand.getOrganizationId());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "creationTime", demand.getDateCreated());
                serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModifiedTime", demand.getLastModified());
                serializeValueOrException(builder::add, "projectId", demand.getProjectId());

                break;
        }

        return builder.build();
    }

    public Demand deserialize(JsonObject demandJson, DeserializeView view) {
        Demand demand = basicDeserialize(demandJson);

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case jsonFromFabricToObjectInApp:
                JsonArray jsonArray = getValueOrException("attachments", demandJson::getJsonArray);
                List<AttachmentEntity> attachments = jsonArray.stream()
                        .map(v -> (JsonObject) v)
                        .map(jAtt -> attachmentDataConverter.deserialize(jAtt, view))
                        .collect(Collectors.toList());

                demand.setAttachmentEntities(new ArrayList<>(attachments));
                List<String> actions = getValueOrException("availableActions", demandJson::getJsonArray, SerializationHelper::deserializeToArray);
                demand.setAvailableActions(actions);
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case objectBetweenChaincodes:
                demand.setId(getValueOrException("id", demandJson::getString));
                demand.setCreatorId(getValueOrException("creatorId", demandJson::getString));
                demand.setOrganizationId(getValueOrException("organizationId", demandJson::getString));
                demand.setDateCreated(getValueOrException("creationTime", demandJson::getString, SerializationHelper::convertToDate));
                demand.setLastModified(getValueOrException("lastModifiedTime", demandJson::getString, SerializationHelper::convertToDate));
                demand.setProjectId(getValueOrException("projectId", demandJson::getString));
                break;
        }

        return demand;
    }

    @Override
    public String getAssignedType() {
        return Demand.class.getSimpleName();
    }

    private Demand basicDeserialize(JsonObject demandJson) {
        Demand demand = new Demand();
        demand.setName(getValueOrException("name", demandJson::getString));
        demand.setBudget(getValueOrException("budget", demandJson::getString, SerializationHelper::convertToBigDecimal));
        demand.setDescription(demandJson.getString("description", ""));
        demand.setEndDate(getValueOrException("endDate", demandJson::getString, SerializationHelper::convertToDate));
        demand.setMessageBoardUrl(demandJson.getString("messageBoardUrl", ""));
        demand.setPriority(Priority.valueOf(demandJson.getString("priority", Priority.LOW.name()).toUpperCase()));
        demand.setTargetAccount(getValueOrException("targetAccount", demandJson::getString));
        demand.setState(getValueOrException("state", demandJson::getString, ObjectState::valueOf));
        return demand;
    }

    private JsonObjectBuilder basicSerialize(Demand demand) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        serializeValueOrDefault(builder::add, "description", demand.getDescription(), "");
        serializeValueOrDefault(builder::add, "messageBoardUrl", demand.getMessageBoardUrl(), "");
        serializeValueOrException(builder::add, Priority::name, "priority", demand.getPriority());
        serializeValueOrException(builder::add, "name", demand.getName());
        serializeValueOrException(builder::add, SerializationHelper::serializeBigDecimalCurrency, "budget", demand.getBudget());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "endDate", demand.getEndDate());
        serializeValueOrException(builder::add, "targetAccount", demand.getTargetAccount());
        serializeValueOrException(builder::add, ObjectState::name, "state", demand.getState());

        return builder;
    }
}
