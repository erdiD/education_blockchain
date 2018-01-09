package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.PeerRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTarget;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.BooleanMessage;
import de.deutschebahn.ilv.smartcontract.commons.model.ObjectList;
import de.deutschebahn.ilv.smartcontract.commons.model.ProjectField;
import de.deutschebahn.ilv.smartcontract.commons.model.StringMessage;
import de.deutschebahn.ilv.smartcontract.offer.*;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ObjectChaincode extends ChaincodeInvocation<Offer> {

    private final static Logger logger = Logger.getLogger(ObjectChaincode.class.getName());
    private final OfferDataConverter offerDataConverter;
    private final ChaincodeTarget counterTarget;
    private final DataConverterProvider dataConverterProvider;

    public ObjectChaincode(ObjectDependenciesFactory<Offer> dependenciesFactory) {
        super(dependenciesFactory);
        offerDataConverter = dependenciesFactory.getDataConverter();
        counterTarget = dependenciesFactory.getChaincodeTargetFactory().getOrCreate(ChaincodeName.PROJECT_CC);
        dataConverterProvider = dependenciesFactory.getDataConverterProvider();
    }

    @Override
    protected ChaincodeResponseMessage invoke(GenericActions action, boolean isUserAction, RequestDependencies<Offer> requestDependencies) {
        OfferFacade offerFacade = requestDependencies.getObjectFacade();
        StateManager<Offer> stateManager = requestDependencies.getStateManager();
        OfferAccessService offerAccessService = requestDependencies.getObjectAccessService();
        List<String> params = requestDependencies.getParams();

        //TODO: set into dependenciesFactory
        StateActionTriggerService<Offer> triggerService = new StateActionTriggerService<>(offerDataConverter, stateManager, requestDependencies.getParams());
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();

        User principal = requestDependencies.getPrincipal();
        ChaincodeResponseMessage response;

        switch (action) {
            case getByProjectId:
                response = getOffers(principal, offerAccessService, offerFacade, availableActionsService, params);
                break;
            case getById:
                response = getOfferById(principal, offerAccessService, offerFacade, availableActionsService, params);
                break;
            case create:
                response = createOffer(principal, offerFacade, params, stateManager);
                break;
            case update:
                response = updateOffer(principal, stateManager, offerFacade, params);
                break;
            case attachEntity:
                response = attachEntity(principal, requestDependencies);
                break;
            case fireAction:
                if (principal != null) {
                    response = triggerService.fireActionAsPrincipal(principal);
                } else {
                    response = triggerService.fireActionAsPeer();
                }
                break;
            case canPerformDirectActionOnProjectOffers:
                response = canPerformDirectActionOnProjectOffers(principal, offerAccessService, new StringMessage(params));
                break;
            case canFireAction:
                response = null;
                break;
            default:
                response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
                break;
        }
        return response;
    }

    private ChaincodeResponseMessage attachEntity(User user, RequestDependencies<Offer> requestDependencies) {
        List<String> params = requestDependencies.getParams();
        String offerId = params.get(0);
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();
        JsonObject jAtt = SerializationHelper.stringToJsonObject(params.get(1));
        OfferFacade offerFacade = requestDependencies.getObjectFacade();
        Offer offer = requestDependencies.getStateManager().actionTriggered(user, offerId, ObjectStateTransitionAction.UPDATE);
        DataConverter<AttachmentEntity> attachmentDataConverter = requestDependencies.getDataConverterForObjectType(AttachmentEntity.class);
        AttachmentEntity attachment = attachmentDataConverter.deserialize(jAtt, DataConverter.DeserializeView.newObjectCreationFromJson);
        attachment = offerFacade.saveAttachment(offerId, attachment);

        offer = offerFacade.prepareObject(offer, availableActionsService, user);
        offer.getAttachmentEntities().add(attachment);
        return new ChaincodeResponseMessage(MessageStatus.OK, offerDataConverter.serialize(offer, DataConverter.SerializeView.objectInFabricToJsonToApp));
    }

    private ChaincodeResponseMessage canPerformDirectActionOnProjectOffers(User user, OfferAccessService offerAccessService, StringMessage stringMessage) {
        boolean canPerform = offerAccessService.canPerformActionOnProjectOffers(user, stringMessage.getValue());
        return new ChaincodeResponseMessage(MessageStatus.OK, new BooleanMessage(canPerform).toJson());
    }

    private ChaincodeResponseMessage getOfferById(User user, OfferAccessService offerAccessService, OfferFacade offerFacade, AvailableActionsService availableActionsService, List<String> params) {
        String offerId = params.get(0);
        Offer offer = offerFacade.getById(offerId).orElseThrow(() -> ClientException.notFoundError(offerId, Offer.class));
        if (!offerAccessService.canRead(user, offer)) {
            throw ClientException.forbiddenException();
        }
        offer = offerFacade.prepareObject(offer, availableActionsService, user);
        JsonObject jsonObject = offerDataConverter.serialize(offer, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private ChaincodeResponseMessage getOffers(User user, OfferAccessService offerAccessService, OfferFacade offerFacade, AvailableActionsService availableActionsService, List<String> params) {
        String id = params.get(0);
        String allOffersId = IdUtils.recreateOfferId(id);
        List<Offer> offers = offerFacade
                .findAll(allOffersId)
                .stream()
                .filter(offer -> offerAccessService.canRead(user, offer))
                .map(offer -> offerFacade.prepareObject(offer, availableActionsService, user))
                .collect(Collectors.toList());
        ObjectList<Offer> offerObjectList = new ObjectList<>(offers, offerDataConverter);
        return new ChaincodeResponseMessage(MessageStatus.OK, offerObjectList.toJson());
    }

    private ChaincodeResponseMessage createOffer(User user, ObjectFacade<Offer> offerFacade, List<String> params, StateManager<Offer> stateManager) {

        if (user == null) {
            throw ClientException.notAuthorized();
        }

        String offerString = params.get(0);
        logger.info("[create] Received json for offer = " + offerString);

        int counter = counterTarget
                .withChaincodeStub(offerFacade.getChaincodeStub())
                .function(GenericActions.getNextId.name())
                .params(Offer.class.getSimpleName())
                .asUser(user.getId())
                .build()
                .execute(jsonObject -> Integer.parseInt(new ProjectField(jsonObject).getValue())).orElse(-1);

        if (counter == -1) {
            throw new RuntimeException("Invalid counter");
        }
        Offer offer = offerDataConverter.deserialize(stringToJsonObject(offerString), DataConverter.DeserializeView.newObjectCreationFromJson);
        offer.setCreatorId(user.getId());
        offer.setOrganizationId(user.getOrganizationId());
        offer.setState(ObjectState.NOT_CREATED);
        CompositeKey projectKey = IdUtils.stringToCompositeKey(offer.getProjectId());
        logger.info("[createOffer] Generating offer key from projectKey=" + projectKey);
        CompositeKey offerKey = IdUtils.generateOfferKey(projectKey, counter);
        offer.setId(offerKey.toString());
        offer = offerFacade.create(offer);
        offer = stateManager.actionTriggered(user, offer, ObjectStateTransitionAction.OPEN_OFFER);
        RemoteCallClient.notifyObjectAccessed(stateManager.getChaincodeStub(), user.getId(), offer.getProjectId());
        JsonObject jsonObject = offerDataConverter.serialize(offer, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private ChaincodeResponseMessage updateOffer(User user, StateManager<Offer> stateManager, ObjectFacade<Offer> offerFacade, List<String> params) {
        String offerString = params.get(0);
        Offer offer = offerDataConverter.deserialize(stringToJsonObject(offerString), DataConverter.DeserializeView.updateObjectFromJson);
        stateManager.actionTriggered(user, offer, ObjectStateTransitionAction.UPDATE);
        String offerId = offer.getId();

        //State cannot change
        ObjectState state = offerFacade.getById(offer.getId()).orElseThrow(() -> ClientException.notFoundError(offerId, Offer.class)).getState();
        offer.setState(state);
        offer = offerFacade.merge(offer);

        JsonObject jsonObject = offerDataConverter.serialize(offer, DataConverter.SerializeView.objectInFabricToJsonToApp);
        RemoteCallClient.notifyObjectAccessed(stateManager.getChaincodeStub(), user.getId(), offer.getProjectId());
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private JsonObject stringToJsonObject(String input) {
        return Json.createReader(new StringReader(input)).readObject();
    }

    //InterCC notifications
    @Override
    public void registerObjectTypesToBeNotified(ObjectUpdatedNotifier<Offer> objectUpdatedNotifier) {
        objectUpdatedNotifier.addNotifier(ChaincodeName.PROJECT_CC);
        objectUpdatedNotifier.addNotifier(ChaincodeName.OFFER_CC);
        objectUpdatedNotifier.addNotifier(ChaincodeName.DEMAND_CC);
        objectUpdatedNotifier.addNotifier(ChaincodeName.CONTRACT_CC);
    }

    private void localNotificationHandler(RequestDependencies<Offer> dependencies, Offer offer) {
        logger.info("[localNotificationHandler] Executing local notification. # Offer=" + offer);
        dependencies.getHandler(offer.getClass().getSimpleName()).ifPresent(h -> h.handle(offer));
    }

    @Override
    public void loadHandlers(RequestDependencies<Offer> dependencies) {
        dependencies.addHandler(Contract.class.getSimpleName(), new ContractUpdatedHandler(
                dependencies.getStateManager(),
                new PeerRoleChecker(),
                dependencies.getObjectFacade(),
                dependencies.getChaincodeStub(),
                dataConverterProvider.getDataConverter(Contract.class.getSimpleName())));

        dependencies.addHandler(Demand.class.getSimpleName(), new DemandUpdatedHandler(
                dependencies.getStateManager(),
                new PeerRoleChecker(),
                dependencies.getObjectFacade(),
                dependencies.getChaincodeStub(),
                dataConverterProvider.getDataConverter(Demand.class.getSimpleName())));

        dependencies.addHandler(Offer.class.getSimpleName(), new OfferUpdatedHandler(
                dependencies.getStateManager(),
                new PeerRoleChecker(),
                dependencies.getObjectFacade(),
                dependencies.getChaincodeStub(),
                dataConverterProvider.getDataConverter(Offer.class.getSimpleName())));

        //TODO here because needs requestDependencies. Flow is now not good for local handling
        //It can be here because a local notification happens in the scope of the same request
        dependencies.getObjectDependencies().getObjectUpdatedNotifier().setLocalNotifier(offer -> localNotificationHandler(dependencies, offer));

    }

    public static void main(String[] args) {
        OfferDataConverter dataConverter = new OfferDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Offer> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Offer> objectFacadeFactory = OfferFacade::new;
        StateManager.StateManagerFactory<Offer> stateManagerFactory = StateManager::new;
        ObjectFlow objectFlow = new OfferILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(objectFlow.getFlowStepsAsSet(), userRoleCheckerFactory);
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new OfferAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Offer> actionTriggerServiceFactory = (sm, params) -> new StateActionTriggerService<>(dataConverter, sm, params);

        ObjectDependenciesFactory<Offer> dependenciesFactory = new ObjectDependenciesFactory<>(
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

        new ObjectChaincode(dependenciesFactory).start(args);
    }

}
