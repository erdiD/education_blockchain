package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.HistoryEntry;
import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 16.10.2017.
 */
public class ActionPerformedEvent {
    public static final String NAME = "ActionPerformedEvent";
    private HistoryEntry historyEntry;
    private UserDataConverter userDataConverter = new UserDataConverter();

    public ActionPerformedEvent(HistoryEntry historyEntry) {
        Objects.requireNonNull(historyEntry);
        this.historyEntry = historyEntry;
    }

    public ActionPerformedEvent(JsonObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        fromJson(jsonObject);
    }

    private void fromJson(JsonObject json) {

        historyEntry = new HistoryEntry();
        historyEntry.setAction(SerializationHelper.getValueOrException("action", json::getString, ObjectStateTransitionAction::valueOf));
        historyEntry.setCreationTime(SerializationHelper.getValueOrException("creationTime", json::getString, SerializationHelper::convertToDate));
        historyEntry.setMarketRole(SerializationHelper.getValueOrException("role", json::getString, MarketRoleName::valueOf));
        historyEntry.setOldState(SerializationHelper.getValueOrException("oldState", json::getString, ObjectState::valueOf));
        historyEntry.setNewState(SerializationHelper.getValueOrException("newState", json::getString, ObjectState::valueOf));
        historyEntry.setProjectId(SerializationHelper.getValueOrException("projectId", json::getString));
        historyEntry.setObjectId(SerializationHelper.getValueOrException("objectId", json::getString));
        historyEntry.setUser(SerializationHelper.getValueOrException(
                "user", json::getJsonObject,
                j -> userDataConverter.deserialize(j, DataConverter.DeserializeView.jsonFromFabricToObjectInApp))
        );
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("action", historyEntry.getAction().name())
                .add("creationTime", SerializationHelper.convertFromDate(historyEntry.getCreationTime()))
                .add("role", historyEntry.getMarketRole().name())
                .add("oldState", historyEntry.getOldState().name())
                .add("newState", historyEntry.getNewState().name())
                .add("projectId", historyEntry.getProjectId())
                .add("objectId", historyEntry.getObjectId());

        if (historyEntry.getUser() != null) {
            builder.add("user", userDataConverter.serialize(historyEntry.getUser(), DataConverter.SerializeView.objectInFabricToJsonToApp));
        }

        return builder.build();

    }

    public HistoryEntry getHistoryEntry() {
        return historyEntry;
    }
}
