package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeInvocationMessage;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectNotification;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.BooleanMessage;
import de.deutschebahn.ilv.smartcontract.commons.model.StringMessage;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 02.11.2017.
 */
public abstract class ChaincodeInvocation<T extends BusinessObject> extends ChaincodeBase implements InterChaincodeCommunication<T> {

    private static final Logger logger = Logger.getLogger(ChaincodeInvocation.class.getName());

    private final ObjectDependenciesFactory<T> dependenciesFactory;
    private UserPrincipalService userPrincipalService;

    public ChaincodeInvocation(ObjectDependenciesFactory<T> dependenciesFactory) {
        this.dependenciesFactory = dependenciesFactory;
        registerObjectTypesToBeNotified(dependenciesFactory.getObjectUpdatedNotifier());
    }

    @Override
    public Response init(ChaincodeStub chaincodeStub) {
        return newSuccessResponse("Initialized");
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            ChaincodeInvocationMessage receivedMessage = new ChaincodeInvocationMessage(stub);
            logger.info("[invoke] Received message " + receivedMessage);
            User principal = loadPrincipal(stub, receivedMessage);
            RequestDependencies<T> requestDependencies = loadRequestDependencies(stub, principal, receivedMessage);
            loadHandlers(requestDependencies);
            ChaincodeResponseMessage responseMessage;
            switch (requestDependencies.getAction()) {
                //TODO: should be handled in an specific class
                case objectUpdated:
                    responseMessage = handleObjectUpdated(requestDependencies, requestDependencies.getParams());
                    break;
                //TODO: should be handled in an specific class
                case canFireAction:
                    //TODO: Should be in a accessRequestHandler?
                    responseMessage = canFireAction(
                            principal,
                            dependenciesFactory.createObjectFacade(stub),
                            requestDependencies.getAvailableActionsService(),
                            new StringMessage(receivedMessage.getParams()).getValue());
                    break;
                default:
                    responseMessage = invoke(
                            requestDependencies.getAction(),
                            receivedMessage.getType() == ChaincodeInvocationMessage.Type.USER,
                            loadRequestDependencies(stub, principal, receivedMessage)
                    );
                    break;
            }

            responseMessage.setMessageId(receivedMessage.getMessageId());
            logger.info("[invoke] Sending response # response=" + responseMessage);
            return newSuccessResponse(responseMessage.asBytes());
        } catch (ClientException e) {
            logger.log(Level.INFO, "[invoke] ClientException produced # Exception=" + e.getMessage(), e);
            return dependenciesFactory.getExceptionMapper().handleExceptionAndGetResponse(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return dependenciesFactory.getExceptionMapper().handleExceptionAndGetResponse(e);
        }
    }

    protected ChaincodeResponseMessage canFireAction(User principal, ObjectFacade<T> facade, AvailableActionsService availableActionsService, String objectId) {
        boolean available = facade
                .getById(objectId)
                .map(obj -> availableActionsService.canPerformSomeAction(principal, obj))
                .orElse(false);

        return new ChaincodeResponseMessage(MessageStatus.OK, new BooleanMessage(available).toJson());
    }

    protected abstract ChaincodeResponseMessage invoke(GenericActions action, boolean isUserAction, RequestDependencies<T> requestDependencies);

    protected RequestDependencies<T> loadRequestDependencies(ChaincodeStub stub, User principal, ChaincodeInvocationMessage receivedMessage) {

        ObjectFacade<T> facade = dependenciesFactory.createObjectFacade(stub);
        ObjectAccessService<T> objectAccessService = null;

        if (principal != null) {
            objectAccessService = dependenciesFactory.createObjectAccessService(stub);
        }

        RequestDependencies<T> requestDependencies = new RequestDependencies<>(
                stub,
                dependenciesFactory,
                userPrincipalService,
                facade,
                objectAccessService,
                receivedMessage.getParams(),
                principal);

        return requestDependencies;
    }

    public ChaincodeResponseMessage handleObjectUpdated(RequestDependencies<T> requestDependencies, List<String> params) {
        ObjectNotification objectNotification = new ObjectNotification(SerializationHelper.stringToJsonObject(params.get(0)));
        logger.info("[handleObjectUpdated] Received notification. ObjectNotification=" + objectNotification);
        DataConverter<?> dataConverter = dependenciesFactory.getDataConverterProvider().getDataConverter(objectNotification.getObjectType());
        Object o = dataConverter.deserialize(objectNotification.getObject(), DataConverter.DeserializeView.objectBetweenChaincodes);
        requestDependencies.getHandler(objectNotification.getObjectType()).ifPresent(h -> h.handle((BusinessObject) o));
        return new ChaincodeResponseMessage(MessageStatus.OK);
    }

    private User loadPrincipal(ChaincodeStub stub, ChaincodeInvocationMessage receivedMessage) {
        User principal = null;
        if (receivedMessage.getType() == ChaincodeInvocationMessage.Type.USER) {
            userPrincipalService = dependenciesFactory.createUserPrincipalService(stub, receivedMessage.getPrincipalId());
            principal = userPrincipalService.loadUser();
            if (principal == null) {
                throw ClientException.forbiddenException();
            }
        }

        return principal;
    }
}
