package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeInvocationMessage;
import de.deutschebahn.ilv.smartcontract.business.remote.ExceptionMapper;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.BooleanMessage;
import de.deutschebahn.ilv.smartcontract.commons.model.ProjectField;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonString;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.deutschebahn.ilv.smartcontract.business.IdUtils.bytesToInt;
import static de.deutschebahn.ilv.smartcontract.business.IdUtils.intToBytes;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ObjectChaincode extends ChaincodeBase {

    private Logger logger = Logger.getLogger(ObjectChaincode.class.getName());
    private ExceptionMapper exceptionMapper;

    private static final List<String> validObjects = Arrays.asList("offer", "delivery", "payment", "attachment");

    public ObjectChaincode() {
        exceptionMapper = new ExceptionMapper();
    }

    @Override
    public Response init(ChaincodeStub chaincodeStub) {
        return newSuccessResponse("Initialized");
    }

    @Override
    public Response invoke(ChaincodeStub chaincodeStub) {
        ChaincodeInvocationMessage receivedMessage = new ChaincodeInvocationMessage(chaincodeStub);
        checkAndInitCounters(chaincodeStub);
        ProjectChaincodeAction functionName = ProjectChaincodeAction.valueOf(receivedMessage.getFunction());
        ChaincodeResponseMessage response;

        try {
            switch (functionName) {
                case getNextId: {
                    response = getNextId(chaincodeStub, receivedMessage.getParams());
                    break;
                }
                case setNextId: {
                    response = setNextId(chaincodeStub, receivedMessage.getParams());
                    break;
                }
                case getCurrentValue: {
                    String objectType = chaincodeStub.getParameters().get(0);
                    response = getCurrentValue(chaincodeStub, objectType, generateCounterId(objectType));
                    break;
                }
                case getProjectField: {
                    String projectId = receivedMessage.getParams().get(0);
                    String fieldName = receivedMessage.getParams().get(1);
                    response = getProjectField(chaincodeStub, projectId, fieldName);
                    break;
                }
                case setProjectField: {
                    response = setProjectField(chaincodeStub, receivedMessage.getParams());
                    break;
                }
                case userHasAccessedObject:
                    response = userHasAccessed(receivedMessage.getPrincipalId(), receivedMessage.getParams().get(0), chaincodeStub);
                    break;
                case addAllowedReadUser:
                    response = addAllowedReadUser(receivedMessage.getPrincipalId(), receivedMessage.getParams().get(0), chaincodeStub);
                    break;
                default:
                    response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
                    break;
            }

            response.setMessageId(receivedMessage.getMessageId());
            logger.info("[invoke] Sending response # response=" + response);
            return newSuccessResponse(response.asBytes());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return exceptionMapper.handleExceptionAndGetResponse(e);
        }
    }

    private ChaincodeResponseMessage addAllowedReadUser(String userId, String projectId, ChaincodeStub stub) {
        CompositeKey accessedUsersKey = new CompositeKey(projectId, "accessedUsers");

        byte[] bytes = stub.getState(accessedUsersKey.toString());
        JsonObject jsonObject;
        if (bytes == null || bytes.length == 0) {
            jsonObject = Json.createObjectBuilder().add("accessedUsers", Json.createArrayBuilder()).build();
        } else {
            jsonObject = SerializationHelper.bytesToJsonObject(bytes);
        }

        Set<String> usersId = jsonObject.getJsonArray("accessedUsers")
                .stream()
                .map(v -> (JsonString) v)
                .map(v -> v.getString())
                .collect(Collectors.toSet());

        usersId.add(userId);
        JsonArrayBuilder builder = usersId.stream().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add);
        JsonObject jsonObject1 = Json.createObjectBuilder().add("accessedUsers", builder).build();
        stub.putState(accessedUsersKey.toString(), jsonObject1.toString().getBytes());
        return new ChaincodeResponseMessage(MessageStatus.OK);
    }

    public ChaincodeResponseMessage userHasAccessed(String userId, String projectId, ChaincodeStub stub) {
        CompositeKey accessedUsersKey = new CompositeKey(projectId, "accessedUsers");

        byte[] bytes = stub.getState(accessedUsersKey.toString());
        boolean canAccess = false;
        if (bytes != null) {
            JsonObject jsonObject = SerializationHelper.bytesToJsonObject(bytes);
            canAccess = jsonObject.getJsonArray("accessedUsers").stream()
                    .map(v -> (JsonString) v)
                    .map(JsonString::getString)
                    .anyMatch(v -> v.equalsIgnoreCase(userId));
        }

        return new ChaincodeResponseMessage(MessageStatus.OK, new BooleanMessage(canAccess).toJson());
    }

    private JsonObject getProject(ChaincodeStub chaincodeStub, String id) {
        return Json.createReader(new ByteArrayInputStream(chaincodeStub.getState(id))).readObject();
    }

    private void checkAndInitCounters(ChaincodeStub chaincodeStub) {
        validObjects.stream().map(ObjectChaincode::generateCounterId).forEach(s -> initCounter(chaincodeStub, s));
    }

    private void initCounter(ChaincodeStub chaincodeStub, String counterId) {
        byte[] counterValue = chaincodeStub.getState(counterId);
        if (counterValue == null || counterValue.length == 0) {
            logger.info("[initCounter] initCounter not set. Initializing ...");
            chaincodeStub.putState(counterId, intToBytes(0));
        } else {
            logger.info("[initCounter] Counter value of " + counterId
                    + " is already set with value=" + bytesToInt(counterValue));
        }
    }

    private ChaincodeResponseMessage getNextId(ChaincodeStub stub, List<String> params) {
        String objectType = params.get(0);
        //TODO: describe counters keys correctly
        String key = objectType;
        if (params.size() > 1) {
            key = params.get(1);
        }

        String id = generateCounterId(key);
        id = id.toLowerCase();
        int counter = 0;
        if (stub.getState(id).length > 0) {
            counter = bytesToInt(stub.getState(id));
        }

        stub.putState(id, intToBytes((counter + 1)));
        logger.info("[getNextId] Id=" + id + " increased its counter. New value is=" + (counter + 1));
        return new ChaincodeResponseMessage(MessageStatus.OK, new ProjectField(id, String.valueOf(counter)).toJson());
    }

    private ChaincodeResponseMessage setNextId(ChaincodeStub stub, List<String> params) {

        String objectType = params.get(0);
        //TODO: describe counters keys correctly
        String key = objectType;
        String value = params.get(1);
        if (params.size() > 2) {
            key = params.get(1);
            value = params.get(2);
        }
        int counter = Integer.valueOf(value);
        String id = generateCounterId(key);
        id = id.toLowerCase();
        stub.putState(id, intToBytes((counter + 1)));

        return new ChaincodeResponseMessage(MessageStatus.OK);
    }

    private ChaincodeResponseMessage getCurrentValue(ChaincodeStub chaincodeStub, String objectType, String id) {
        return new ChaincodeResponseMessage(MessageStatus.OK, new ProjectField(
                String.valueOf(fetchCurrentValue(chaincodeStub, objectType, id))
        ).toJson());
    }

    private int fetchCurrentValue(ChaincodeStub chaincodeStub, String objectType, String id) {
        if (!isValidObjectType(objectType)) {
            throw ClientException.invalidValue("ObjectType", objectType);
        }
        byte[] bytes = chaincodeStub.getState(id);
        return bytesToInt(bytes);
    }

    private ChaincodeResponseMessage getProjectField(ChaincodeStub stub, String projectId, String fieldName) {
        CompositeKey compositeKey = IdUtils.getFieldKey(projectId, fieldName);
        String value = new String(stub.getState(compositeKey.toString()));
        return new ChaincodeResponseMessage(MessageStatus.OK, new ProjectField(fieldName, value).toJson());
    }

    private ChaincodeResponseMessage setProjectField(ChaincodeStub chaincodeStub, List<String> params) {
        String projectId = params.get(0);
        ProjectField projectField = new ProjectField(params.get(1));
        CompositeKey compositeKey = IdUtils.getFieldKey(projectId, projectField.getFieldName());
        chaincodeStub.putState(compositeKey.toString(), projectField.getValue().getBytes());
        return new ChaincodeResponseMessage(MessageStatus.OK);
    }

    private static String generateCounterId(String object) {
        return object + "_counter";
    }

    public static void main(String[] args) {
        new ObjectChaincode().start(args);
    }

    private boolean isValidObjectType(String objectType) {
        return validObjects.contains(objectType);
    }
}
