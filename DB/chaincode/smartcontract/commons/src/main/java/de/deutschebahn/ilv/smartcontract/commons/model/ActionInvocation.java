package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 14.10.2017.
 */
public class ActionInvocation {

    private ObjectStateTransitionAction action;
    private String objectId;

    public ActionInvocation(ObjectStateTransitionAction action, String objectId) {
        this.action = action;
        this.objectId = objectId;
    }

    public ActionInvocation(List<String> params) {
        objectId = params.get(0);
        action = ObjectStateTransitionAction.valueOf(params.get(1));
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("objectId", objectId)
                .add("action", action.name())
                .build();
    }

    public List<String> asParamList() {
        return Arrays.asList(objectId, action.name());
    }

    public ObjectStateTransitionAction getAction() {
        return action;
    }

    public String getObjectId() {
        return objectId;
    }
}
