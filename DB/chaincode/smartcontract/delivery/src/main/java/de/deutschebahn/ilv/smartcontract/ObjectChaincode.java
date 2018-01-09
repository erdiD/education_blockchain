package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTarget;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.BooleanMessage;
import de.deutschebahn.ilv.smartcontract.commons.model.DeliveryEntrySet;
import de.deutschebahn.ilv.smartcontract.commons.model.ProjectField;
import de.deutschebahn.ilv.smartcontract.commons.model.StringList;
import de.deutschebahn.ilv.smartcontract.delivery.chaincode.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.JsonObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ObjectChaincode extends ChaincodeInvocation<Delivery> {

    private static final Logger logger = Logger.getLogger(ObjectChaincode.class.getName());
    private final DataConverterProvider dataConverterProvider;
    private final DeliveryDataConverter dataConverter;

    public ObjectChaincode(ObjectDependenciesFactory<Delivery> objectDependenciesFactory) {
        super(objectDependenciesFactory);
        dataConverter = objectDependenciesFactory.getDataConverter();
        dataConverterProvider = objectDependenciesFactory.getDataConverterProvider();
    }

    @Override
    public Response init(ChaincodeStub chaincodeStub) {
        return newSuccessResponse("Initialized");
    }

    @Override
    protected ChaincodeResponseMessage invoke(GenericActions action, boolean isUserAction, RequestDependencies<Delivery> requestDependencies) {
        DeliveryFacade deliveryFacade = requestDependencies.getObjectFacade();
        StateManager<Delivery> stateManager = requestDependencies.getStateManager();
        DeliveryAccessService deliveryAccessService = requestDependencies.getObjectAccessService();
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();
        StateActionTriggerService<Delivery> triggerService = new StateActionTriggerService<>(dataConverter, stateManager, requestDependencies.getParams());

        List<String> params = requestDependencies.getParams();
        ChaincodeResponseMessage response;
        User principal = requestDependencies.getPrincipal();

        switch (action) {
            case getById:
                response = getById(principal, deliveryAccessService, deliveryFacade, availableActionsService, params);
                break;
            case update:
                response = update(principal, stateManager, params);
                break;
            case fireAction:
                if (isUserAction) {
                    response = triggerService.fireActionAsPrincipal(principal);
                } else {
                    response = triggerService.fireActionAsPeer();
                }
                break;
            case canRead:
                response = canRead(principal, deliveryFacade, deliveryAccessService, params);
                break;
            case saveNewDeliveryEntries:
                response = saveNewDeliveryEntries(principal, stateManager, deliveryFacade, params);
                break;
            default:
                response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
                break;
        }

        return response;
    }

    private ChaincodeResponseMessage addDeliveryPsp(User user, DeliveryFacade deliveryFacade,
                                                    StateManager<Delivery> stateManager, List<String> params) {
        String deliveryId = params.get(0);
        StringList objectList = new StringList(params.get(1));
        Delivery delivery = stateManager.actionTriggered(user, deliveryId, ObjectStateTransitionAction.ADD_DELIVERY_PSP);
        deliveryFacade.addPsps(delivery, objectList.getValues());

        return new ChaincodeResponseMessage(MessageStatus.OK);
    }

    private ChaincodeResponseMessage canRead(User user, DeliveryFacade deliveryFacade, DeliveryAccessService deliveryAccessService, List<String> params) {
        String projectId = params.get(0);
        Delivery delivery = deliveryFacade.getByProjectId(projectId).orElse(null);
        BooleanMessage access;
        if (delivery == null) {
            logger.warning("[canRead] Delivery not found # projectId=" + projectId);
            access = new BooleanMessage(false);
        } else {
            access = new BooleanMessage(deliveryAccessService.canRead(user, delivery));
        }
        return new ChaincodeResponseMessage(MessageStatus.OK, access.toJson());
    }

    private ChaincodeResponseMessage getById(User user, DeliveryAccessService deliveryAccessService, DeliveryFacade deliveryFacade, AvailableActionsService availableActionsService, List<String> params) {
        String id = params.get(0);
        Delivery delivery = deliveryFacade.getById(id).orElseThrow(() -> ClientException.notFoundError(id, Delivery.class));

        if (user != null && !deliveryAccessService.canRead(user, delivery)) {
            throw ClientException.notAuthorized();
        }
        delivery = deliveryFacade.prepareObject(delivery, availableActionsService, user);
        JsonObject jsonObject = dataConverter.serialize(delivery, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private ChaincodeResponseMessage saveNewDeliveryEntries(User user, StateManager<Delivery> deliveryStateManager, DeliveryFacade deliveryFacade, List<String> params) {
        String projectId = params.get(0);
        String deliveryId = IdUtils.recreateDeliveryId(projectId);
        deliveryStateManager.actionTriggered(user, deliveryId, ObjectStateTransitionAction.CREATE_DELIVERY_ENTRY);
        DeliveryEntrySet deliveryEntrySet = new DeliveryEntrySet(params.get(1), new AttachmentDataConverter(), new DeliveryEntryDataConverter(), DataConverter.DeserializeView.newObjectCreationFromJson);

        //TODO: inject dependency correctly
        AttachmentFacade attachmentFacade = new AttachmentFacade(new AttachmentDataConverter(), deliveryFacade.getChaincodeStub());
        AttachmentEntity attachmentEntity = deliveryEntrySet.getAttachmentEntity();
        attachmentEntity.setId(getNextAttachmentId(deliveryFacade.getChaincodeStub(), user.getId(), deliveryId));
        AttachmentEntity persistedAttachmentEntity = attachmentFacade.merge(attachmentEntity);

        AtomicInteger deliveryEntryCounter = new AtomicInteger(getNextDeliveryEntryCounter(deliveryFacade.getChaincodeStub(), user.getId(), projectId));
        List<DeliveryEntry> deliveryEntries = deliveryEntrySet.getDeliveryEntries();

        //TODO: inject dependency correctly
        DeliveryEntryFacade deliveryEntryFacade = new DeliveryEntryFacade(new DeliveryEntryDataConverter(), deliveryFacade.getChaincodeStub());
        List<DeliveryEntry> persistedEntries = deliveryEntries.stream().map(deliveryEntry -> {
            String deliveryEntryId = IdUtils.generateDeliveryEntryKey(projectId, deliveryEntryCounter.getAndIncrement()).toString();
            deliveryEntry.setId(deliveryEntryId);
            deliveryEntry.setAttachmentEntityId(persistedAttachmentEntity.getId());
            deliveryEntry.setFileName(persistedAttachmentEntity.getFileName());
            deliveryEntry.setHash(persistedAttachmentEntity.getHash());
            logger.info("[saveNewDeliveryEntries] Persisting deliveryEntry # deliveryEntry=" + deliveryEntry);
            return deliveryEntryFacade.merge(deliveryEntry);
        }).collect(Collectors.toList());

        setNextDeliveryEntryCounter(deliveryFacade.getChaincodeStub(), user.getId(), projectId, deliveryEntryCounter.get());

        DeliveryEntrySet responseDeliveryEntrySet =
                new DeliveryEntrySet(attachmentEntity, persistedEntries, new AttachmentDataConverter(), new DeliveryEntryDataConverter(), DataConverter.SerializeView.objectInFabricToJsonToApp);

        return new ChaincodeResponseMessage(MessageStatus.OK, responseDeliveryEntrySet.toJson());
    }

    //TODO: deduplicate code
    private String getNextAttachmentId(ChaincodeStub stub, String userId, String deliveryId) {
        int counter = new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.getNextId.name())
                .params(AttachmentEntity.class.getSimpleName(), deliveryId)
                .asUser(userId)
                .build()
                .execute(jsonObject -> Integer.parseInt(new ProjectField(jsonObject).getValue())).orElse(-1);

        return IdUtils.generateAttachmentKey(deliveryId, counter).toString();
    }

    private int getNextDeliveryEntryCounter(ChaincodeStub stub, String userId, String projectId) {
        return new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.getNextId.name())
                .params(DeliveryEntry.class.getSimpleName(), projectId)
                .asUser(userId)
                .build()
                .execute(jsonObject -> Integer.parseInt(new ProjectField(jsonObject).getValue()))
                .orElseThrow(() -> new RuntimeException("Not possible to generate next counter for delivery entry # projectId=" + projectId));

    }

    private void setNextDeliveryEntryCounter(ChaincodeStub stub, String userId, String projectId, int counter) {
        new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.setNextId.name())
                .params(DeliveryEntry.class.getSimpleName(), projectId, String.valueOf(counter))
                .asUser(userId)
                .build()
                .execute();

    }

    private ChaincodeResponseMessage update(User user, StateManager<Delivery> stateManager, List<String> params) {
        String deliveryString = params.get(0);
        Delivery delivery = dataConverter.deserialize(SerializationHelper.stringToJsonObject(deliveryString), DataConverter.DeserializeView.updateObjectFromJson);
        stateManager.actionTriggered(user, delivery, ObjectStateTransitionAction.UPDATE);
        JsonObject jsonObject = dataConverter.serialize(delivery, DataConverter.SerializeView.objectInFabricToJsonToApp);

        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

//    private ChaincodeResponseMessage fireAction(User user, StateManager<Delivery> stateManager, ActionInvocation actionInvocation) {
//
//        String deliveryId = actionInvocation.getObjectId();
//        ObjectStateTransitionAction action = actionInvocation.getAction();
//
//        Delivery delivery;
//        if (user == null) {
//            delivery = stateManager.peerTriggerOption(deliveryId, action);
//        } else {
//            delivery = stateManager.actionTriggered(user, deliveryId, action);
//        }
//
//        JsonObject jsonObject = dataConverter.serialize(delivery, DataConverter.SerializeView.objectInFabricToJsonToApp);
//        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
//    }

    @Override
    public void registerObjectTypesToBeNotified(ObjectUpdatedNotifier<Delivery> objectUpdatedNotifier) {
        objectUpdatedNotifier.addNotifier(ChaincodeName.CONTRACT_CC);
    }

    @Override
    public void loadHandlers(RequestDependencies<Delivery> dependencies) {
        dependencies.addHandler(Contract.class.getSimpleName(), new ContractUpdatedHandler(
                dependencies.getStateManager(),
                dependencies.getObjectFacade(),
                dependencies.getChaincodeStub(),
                dataConverterProvider.getDataConverter(Contract.class.getSimpleName())));
    }

    public static void main(String[] args) {

        DeliveryDataConverter dataConverter = new DeliveryDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Delivery> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Delivery> objectFacadeFactory = DeliveryFacade::new;
        StateManager.StateManagerFactory<Delivery> stateManagerFactory = StateManager::new;
        ObjectFlow objectFlow = new DeliveryILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(objectFlow.getFlowStepsAsSet(), userRoleCheckerFactory);
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new DeliveryAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Delivery> actionTriggerServiceFactory = (sm, params) -> new StateActionTriggerService<>(dataConverter, sm, params);

        ObjectDependenciesFactory<Delivery> dependencies = new ObjectDependenciesFactory<>(
                objectFlow,
                dataConverter,
                chaincodeTargetFactory,
                objectUpdatedNotifier,
                dataConverterProvider,
                userRoleCheckerFactory,
                accessServiceFactory,
                userPrincipalServiceFactory,
                objectFacadeFactory,
                stateManagerFactory,
                availableActionsService,
                actionTriggerServiceFactory);

        new ObjectChaincode(dependencies).start(args);
    }
}
