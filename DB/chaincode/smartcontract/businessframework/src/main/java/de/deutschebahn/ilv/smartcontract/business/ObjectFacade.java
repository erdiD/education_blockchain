package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.Persistable;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class ObjectFacade<T extends Persistable> {

    private final Logger logger = Logger.getLogger(getClass().getName() + "#" + ObjectFacade.class.getSimpleName());
    protected final DataConverter<T> dataConverter;
    protected final ChaincodeStub chaincodeStub;

    protected ObjectFacade(DataConverter<T> dataConverter, ChaincodeStub chaincodeStub) {
        Objects.requireNonNull(dataConverter);
        this.chaincodeStub = chaincodeStub;
        this.dataConverter = dataConverter;
    }

    public Optional<T> getById(String id) {
        byte[] objectBytes = chaincodeStub.getState(id);

        if (objectBytes == null || objectBytes.length == 0) {
            logger.info(String.format("[getById] Object not found. Id=%s", id));
            return Optional.empty();
        }

        JsonObject jsonObject = stringToJsonObject(new String(objectBytes));
        logger.info(String.format("[getById] Getting state key:%s, value:%s", id, jsonObject));
        T object = dataConverter.deserialize(jsonObject, DataConverter.DeserializeView.jsonInDatabaseToObjectInFabric);

        return Optional.of(object);
    }

    public List<T> findAll() {
        return findAll("");
    }

    public List<T> findAll(String partialKey) {
        QueryResultsIterator<KeyValue> keyValues = chaincodeStub.getStateByPartialCompositeKey(partialKey);
        ArrayList<T> objects = new ArrayList<>();

        for (KeyValue keyValue : keyValues) {
            JsonObject jsonObject = stringToJsonObject(new String(keyValue.getValue()));
            if (isCorrectType(jsonObject)) {
                T object = dataConverter.deserialize(jsonObject, DataConverter.DeserializeView.jsonInDatabaseToObjectInFabric);
                objects.add(object);
            }
        }

        return objects;
    }

    protected boolean isCorrectType(JsonObject jsonObject) {
        return true;
    }

    public T create(T object) {
        String id = object.getId();
        checkId(id);
        //TODO: remove from here. Is not the same on all peers
        object.setDateCreated(new Date());
        object.setLastModified(object.getDateCreated());
        JsonObject jsonObject = dataConverter.serialize(object, DataConverter.SerializeView.objectInFabricToJsonInDatabase);
        try {
            chaincodeStub.putState(id, jsonObject.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        logger.info(String.format("[create] Saving state key:%s, value:%s", id, jsonObject));
        return object;
    }

    protected void checkId(String objectId) {
        String regex = "^(P_[\\w]{8}-[\\w]{4}-[4][\\w]{3}-[\\w]{4}-[\\w]{12})(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectId);

        if (!matcher.matches()) {
            logger.warning("[checkId] Invalid id passed: " + objectId);
            throw new RuntimeException("[getProjectId] Invalid id passed: " + objectId);
        }
    }

    public T merge(T object) {
        String id = object.getId();

        T persistedObject = getById(id).orElse(null);

        if (persistedObject == null) {
            return create(object);
        }

        //TODO: be aware if it should be reactivated. In the current way received object and persisted object must match
//        persistedObject.updateFromObject(object);
        persistedObject = object;
        //TODO: it must be externally provided. Not same value for all objects
        persistedObject.setLastModified(new Date());
        JsonObject jsonObject = dataConverter.serialize(persistedObject, DataConverter.SerializeView.objectInFabricToJsonInDatabase);
        chaincodeStub.putState(id, jsonObject.toString().getBytes());
        logger.info(String.format("[merge] Saving state key:%s, value:%s", id, jsonObject));
        return persistedObject;
    }

    public String getProjectField(String projectId, String fieldName) {
        Chaincode.Response response = getChaincodeStub().invokeChaincodeWithStringArgs(
                "ProjectChaincode",
                Arrays.asList(projectId, fieldName)
        );

        if (response.getStatus() != Chaincode.Response.Status.SUCCESS) {
            logger.warning("[getProjectField] No valid response received. " + new String(response.getPayload()));
            return "";
        }

        return new String(response.getPayload());
    }

    public void setProjectField(String projectId, String fieldName, String value) {
        Chaincode.Response response = getChaincodeStub().invokeChaincodeWithStringArgs(
                "ProjectChaincode",
                Arrays.asList(projectId, fieldName, value)
        );

        if (response.getStatus() != Chaincode.Response.Status.SUCCESS) {
            logger.warning("[getProjectField] No valid response received. " + new String(response.getPayload()));
        }
    }

    public void remove(T object) {
        getChaincodeStub().delState(object.getId());
    }

    protected JsonObject stringToJsonObject(String input) {
        return Json.createReader(new StringReader(input)).readObject();
    }

    public ChaincodeStub getChaincodeStub() {
        return chaincodeStub;
    }

    public interface ObjectFacadeFactory<T extends BusinessObject> {
        ObjectFacade<T> getObjectFacade(DataConverter<T> dataConverter, ChaincodeStub chaincodeStub);
    }
}
