package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeResponseMessage;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.MessageStatus;

import javax.json.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 01.11.2017.
 */
public class StateActionTriggerService<T extends BusinessObject> {

    final DataConverter<T> dataConverter;
    final StateManager<T> stateManager;
    final List<String> params;

    public StateActionTriggerService(DataConverter<T> dataConverter, StateManager<T> stateManager, List<String> params) {
        Objects.requireNonNull(dataConverter);
        Objects.requireNonNull(stateManager);
        Objects.requireNonNull(params);

        this.dataConverter = dataConverter;
        this.stateManager = stateManager;
        this.params = params;
    }

    public ChaincodeResponseMessage fireActionAsPrincipal(User principal) {
        Objects.requireNonNull(principal);
        return fireAction(principal);
    }

    public ChaincodeResponseMessage fireActionAsPeer() {
        return fireAction(null);
    }

    private ChaincodeResponseMessage fireAction(User principal) {

        String objectId = params.get(0);
        ObjectStateTransitionAction action = ObjectStateTransitionAction.valueOf(params.get(1));

        T object;
        if (principal == null) {
            object = stateManager.peerTriggerOption(objectId, action);
        } else {
            object = stateManager.actionTriggered(principal, objectId, action);
        }

        JsonObject jsonObject = dataConverter.serialize(object, DataConverter.SerializeView.objectInFabricToJsonToApp);
        RemoteCallClient.notifyObjectAccessed(stateManager.getChaincodeStub(), principal.getId(), object.getProjectId());
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    public interface StateActionTriggerServiceFactory<T extends BusinessObject> {
        StateActionTriggerService getStateActionTriggerService(StateManager<T> stateManager, List<String> params);
    }
}