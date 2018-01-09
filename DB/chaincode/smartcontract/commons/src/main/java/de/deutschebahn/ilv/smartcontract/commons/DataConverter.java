package de.deutschebahn.ilv.smartcontract.commons;

import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 30.08.2017.
 */
public interface DataConverter<T> {

    //TODO: Rename for clearness. Better to describe using the level of object readiness (at least until now)
    enum SerializeView {
        createJsonForNewObject,
        objectInAppToJsonToFabric,
        objectInFabricToJsonInDatabase,
        objectInFabricToJsonToApp,
        objectBetweenChaincodes
    }

    enum DeserializeView {
        newObjectCreationFromJson,
        updateObjectFromJson,
        jsonInDatabaseToObjectInFabric,
        jsonFromFabricToObjectInApp,
        objectBetweenChaincodes

    }

    JsonObject serialize(T object, SerializeView view);

    T deserialize(JsonObject jsonObject, DeserializeView view);

    String getAssignedType();
}
