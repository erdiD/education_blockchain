package de.deutschebahn.ilv.smartcontract.client;

import de.deutschebahn.ilv.domain.Persistable;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.ObjectList;
import org.hyperledger.fabric.sdk.ChaincodeID;

import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class AbstractChaincodeClient<T extends Persistable> {

    private static final Logger logger = Logger.getLogger(AbstractChaincodeClient.class.getName());
    private final String loggedUserId;
    private final ChaincodeID chaincodeID;
    private final SmartContractClient client;
    private final DataConverter<T> dataConverter;

    public AbstractChaincodeClient(String loggedUserId, SmartContractClient client, DataConverter<T> dataConverter, ChaincodeID chaincodeID) {
        this.loggedUserId = loggedUserId;
        this.client = client;
        this.chaincodeID = chaincodeID;
        this.dataConverter = dataConverter;
    }

    public CommunicationResult<T> getById(String id) {
        id = replaceToUnicode(id);
        ChaincodeInvocationMessage messageInvocation = new ChaincodeInvocationMessage(GenericActions.getById.name(), loggedUserId, Arrays.asList(id), ChaincodeInvocationMessage.Type.USER);
        ChaincodeResponseMessage messageResponse = queryChaincode(messageInvocation);
        return wrapResult(messageResponse);
    }

    public CommunicationResult<List<T>> findAll() {
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(
                GenericActions.getAll.name()
                , loggedUserId
                , Collections.emptyList()
                , ChaincodeInvocationMessage.Type.USER);

        ChaincodeResponseMessage messageResponse = invoke(invocationMessage);
        ObjectList<T> objectList = new ObjectList<>(messageResponse.getPayload(), dataConverter);
        objectList.getObjects().forEach(this::removeUnicodeCompositeKeySymbol);
        return wrapResult(messageResponse, objectList.getObjects());
    }

    public CommunicationResult<T> create(T object) {
        JsonObject jsonObject = dataConverter.serialize(object, DataConverter.SerializeView.createJsonForNewObject);
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(GenericActions.create.name()
                , loggedUserId
                , Arrays.asList(jsonObject.toString())
                , ChaincodeInvocationMessage.Type.USER);

        ChaincodeResponseMessage messageResponse = invoke(invocationMessage);
        return wrapResult(messageResponse);
    }

    public CommunicationResult<T> update(T object) {
        if (object.getId() == null || object.getId().isEmpty()) {
            throw new RuntimeException("No id given. Updated not possible");
        }
        rebuildUnicodeCompositeKeySymbol(object);
        JsonObject jsonObject = dataConverter.serialize(object, DataConverter.SerializeView.objectInAppToJsonToFabric);
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(GenericActions.update.name()
                , loggedUserId
                , Arrays.asList(jsonObject.toString())
                , ChaincodeInvocationMessage.Type.USER);

        ChaincodeResponseMessage messageResponse = invoke(invocationMessage);
        //TODO: Object should be cloned
        removeUnicodeCompositeKeySymbol(object);
        return wrapResult(messageResponse);
    }

    protected CommunicationResult<T> wrapResult(ChaincodeResponseMessage messageResponse) {
        if (messageResponse.getStatus().isSuccessful()) {
            JsonObject jsonObject = messageResponse.getPayload();
            T object = dataConverter.deserialize(jsonObject, DataConverter.DeserializeView.jsonFromFabricToObjectInApp);
            removeUnicodeCompositeKeySymbol(object);
            return CommunicationResult.success(messageResponse.getStatus(), object);
        } else {
            return CommunicationResult.fail(messageResponse.getStatus(), new ErrorPayload(messageResponse.getPayload()));
        }
    }

    //TODO: force it to use replaceToExternalSymbol
    protected <R> CommunicationResult<R> wrapResult(ChaincodeResponseMessage messageResponse, R object) {
        if (messageResponse.getStatus().isSuccessful()) {
            return CommunicationResult.success(messageResponse.getStatus(), object);
        } else {
            return CommunicationResult.fail(messageResponse.getStatus(), new ErrorPayload(messageResponse.getPayload()));
        }
    }

    public CommunicationResult<Void> remove(String id) {
        throw new UnsupportedOperationException();
    }

    public String getLoggedUserId() {
        return loggedUserId;
    }

    protected ChaincodeResponseMessage queryChaincode(ChaincodeInvocationMessage messageInvocation) {
        byte[] body = client.queryChainCode(chaincodeID, messageInvocation.getFunction(), messageInvocation.getSendParamsAsStringArray());
        JsonObject jsonObject = SerializationHelper.bytesToJsonObject(body);
        return new ChaincodeResponseMessage(jsonObject);
    }

    protected ChaincodeResponseMessage invoke(ChaincodeInvocationMessage invocationMessage) {
        ProposalsResult result = client.invokeChaincode(
                chaincodeID,
                invocationMessage.getFunction(),
                invocationMessage.getSendParamsAsStringArray()
        );

        if (result.successfull()) {
            sendToOrdererIfPossible(result);
        }

        JsonObject jsonObject = bytesToJsonObject(result.getBody());
        return new ChaincodeResponseMessage(jsonObject);
    }

    private TransactionResult sendToOrdererIfPossible(ProposalsResult result) {
        if (result.canBeSendToOrderer()) {
            try {
                TransactionResult transactionResult = client.sendTransactionToOrdererAndConfirm(result).get();
                return transactionResult;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else if (!result.successfull()) {
            throw new RuntimeException("This proposal was not successful. Do not send to orderer!!:" + result.getMessage());
        } else {
            logger.info("[sendToOrdererIfPossible] Proposal cannot be sent to orderer.ProposalsResult=" + result);
            return TransactionResult.createFailedResult(result.getTransactionId(), result.getMessage());
        }
    }

    private JsonObject bytesToJsonObject(byte[] bytes) {
        return SerializationHelper.bytesToJsonObject(bytes);
    }

    protected SmartContractClient getClient() {
        return client;
    }

    protected ChaincodeID getChaincodeID() {
        return chaincodeID;
    }

    protected DataConverter<T> getDataConverter() {
        return dataConverter;
    }

    protected void rebuildUnicodeCompositeKeySymbol(T object) {
        object.setId(replaceToUnicode(object.getId()));
    }

    protected void removeUnicodeCompositeKeySymbol(T object) {
        object.setId(replaceToExternalSymbol(object.getId()));
    }

    public static String replaceToUnicode(String str) {
        return str.replace(IdUtils.EXT_SEPARATOR, IdUtils.HL_SEPARATOR);
    }

    public static String replaceToExternalSymbol(String str) {
        return str.replace(IdUtils.HL_SEPARATOR, IdUtils.EXT_SEPARATOR);
    }
}
