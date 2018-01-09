package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.demand.*;
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
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ObjectChaincode extends ChaincodeInvocation<Demand> {

    private static final Logger logger = Logger.getLogger(ObjectChaincode.class.getName());
    private final DemandDataConverter demandDataConverter;

    public ObjectChaincode(ObjectDependenciesFactory<Demand> dependenciesFactory) {
        super(dependenciesFactory);
        demandDataConverter = dependenciesFactory.getDataConverter();
    }

    @Override
    protected ChaincodeResponseMessage invoke(GenericActions action, boolean isUserAction, RequestDependencies<Demand> requestDependencies) {

        DemandFacade demandFacade = requestDependencies.getObjectFacade();
        StateManager<Demand> stateManager = requestDependencies.getStateManager();
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();
        DemandAccessService demandAccessService = requestDependencies.getObjectAccessService();
        StateActionTriggerService<Demand> triggerService = new StateActionTriggerService<>(demandDataConverter, stateManager, requestDependencies.getParams());
        List<String> params = requestDependencies.getParams();
        User principal = requestDependencies.getPrincipal();

        ChaincodeResponseMessage response;
        switch (action) {
            case getById:
                response = getDemandById(principal, demandAccessService, demandFacade, availableActionsService, params.get(0));
                break;
            case getByProjectId:
                response = getDemandByProjectId(principal, demandAccessService, demandFacade, availableActionsService, params);
                break;
            case getAll:
                response = getAllAccessibleDemands(principal, demandFacade, demandAccessService);
                break;
            case create:
                response = createDemand(principal, demandFacade, params, stateManager);
                break;
            case update:
                response = updateDemand(principal, stateManager, demandFacade, params);
                break;
            case attachEntity:
                response = attachEntity(principal, requestDependencies, params);
                break;
            case fireAction:
                if (principal != null) {
                    response = triggerService.fireActionAsPrincipal(principal);
                } else {
                    response = triggerService.fireActionAsPeer();
                }
                break;
            case canFireAction:
                throw new RuntimeException("Should never happen. Implemented on super class");
            case canAcceptOffers:
                response = canAcceptOffers(requestDependencies.getObjectFacade(), requestDependencies.getObjectAccessService(), new ProjectField(params));
                break;
            default:
                response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
                break;
        }
        return response;
    }

    private ChaincodeResponseMessage getAllAccessibleDemands(User user, DemandFacade demandFacade, DemandAccessService demandAccessService) {
        List<Demand> demands = demandFacade.findAll().stream()
                .filter(demand -> demandAccessService.canRead(user, demand))
                .collect(Collectors.toList());
        ObjectList<Demand> objectList = new ObjectList<>(demands, demandDataConverter);
        return new ChaincodeResponseMessage(MessageStatus.OK, objectList.toJson());
    }

    private ChaincodeResponseMessage canAcceptOffers(DemandFacade demandFacade, DemandAccessService accessService, ProjectField projectField) {
        String projectId = projectField.getValue();
        String demandId = IdUtils.recreateDemandId(projectId);
        Demand demand = demandFacade.getById(demandId).orElseThrow(() -> ClientException.notFoundError(demandId, Demand.class));
        boolean canAccept = accessService.demandCanReceiveOffers(demand);
        return new ChaincodeResponseMessage(MessageStatus.OK, new BooleanMessage(canAccept).toJson());
    }

    private ChaincodeResponseMessage attachEntity(User user, RequestDependencies<Demand> requestDependencies, List<String> params) {
        String demandId = params.get(0);
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();
        JsonObject jAtt = SerializationHelper.stringToJsonObject(params.get(1));
        DemandFacade demandFacade = requestDependencies.getObjectFacade();
        Demand demand = requestDependencies.getStateManager().actionTriggered(user, demandId, ObjectStateTransitionAction.UPDATE);
        DataConverter<AttachmentEntity> attachmentDataConverter = requestDependencies.getDataConverterForObjectType(AttachmentEntity.class);
        AttachmentEntity attachment = attachmentDataConverter.deserialize(jAtt, DataConverter.DeserializeView.newObjectCreationFromJson);
        attachment = demandFacade.saveAttachment(demandId, attachment);
        demand = demandFacade.prepareObject(demand, availableActionsService, user);
        demand.getAttachmentEntities().add(attachment);
        return new ChaincodeResponseMessage(MessageStatus.OK, demandDataConverter.serialize(demand, DataConverter.SerializeView.objectInFabricToJsonToApp));
    }

    private ChaincodeResponseMessage getDemandByProjectId(User user, DemandAccessService demandAccessService,
                                                          DemandFacade demandFacade, AvailableActionsService stateManager,
                                                          List<String> params) {
        return getDemandById(user, demandAccessService, demandFacade, stateManager, IdUtils.recreateDemandId(params.get(0)));
    }

    private ChaincodeResponseMessage getDemandById(User user, DemandAccessService demandAccessService, DemandFacade demandFacade, AvailableActionsService availableActionsService, String demandId) {
        Demand demand = demandFacade.getById(demandId).orElseThrow(() -> ClientException.notFoundError(demandId, Demand.class));

        if (user != null && !demandAccessService.canRead(user, demand)) {
            throw ClientException.forbiddenException();
        }

        demand = demandFacade.prepareObject(demand, availableActionsService, user);
        JsonObject jsonObject = demandDataConverter.serialize(demand, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    public ChaincodeResponseMessage createDemand(User user, DemandFacade demandFacade, List<String> params, StateManager<Demand> stateManager) {

        if (user == null) {
            throw ClientException.notAuthorized();
        }

        CompositeKey demandKey = IdUtils.generateDemandKey();
        String demandString = params.get(0);
        logger.info("[create] Received json for demand = " + demandString);

        Demand demand = demandDataConverter.deserialize(SerializationHelper.stringToJsonObject(demandString), DataConverter.DeserializeView.newObjectCreationFromJson);
        demand.setId(demandKey.toString());
        demand.setState(ObjectState.NOT_CREATED);
        demand.setProjectId(IdUtils.getProjectId(demandKey));
        demand.setCreatorId(user.getId());
        demand.setOrganizationId(user.getOrganizationId());
        demand = demandFacade.create(demand);
        demand = stateManager.actionTriggered(user, demand, ObjectStateTransitionAction.CREATE_DEMAND);
        JsonObject jsonObject = demandDataConverter.serialize(demand, DataConverter.SerializeView.objectInFabricToJsonToApp);
        RemoteCallClient.notifyObjectAccessed(demandFacade.getChaincodeStub(), user.getId(), demand.getProjectId());

        RemoteCallClient.saveProjectField(demandFacade.getChaincodeStub(), user.getId(), demand.getProjectId(), "creatorId", demand.getCreatorId());
        RemoteCallClient.saveProjectField(
                demandFacade.getChaincodeStub()
                , user.getId()
                , demand.getProjectId()
                , "demandBudget"
                , SerializationHelper.serializeBigDecimalCurrency(demand.getBudget())
        );

        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    public ChaincodeResponseMessage updateDemand(User user, StateManager<Demand> stateManager, DemandFacade demandFacade, List<String> params) {

        if (user == null) {
            throw ClientException.notAuthorized();
        }

        String demandString = params.get(0);
        Demand demand = demandDataConverter.deserialize(SerializationHelper.stringToJsonObject(demandString), DataConverter.DeserializeView.updateObjectFromJson);
        String demandId = demand.getId();
        stateManager.actionTriggered(user, demand, ObjectStateTransitionAction.UPDATE);
        //State cannot change
        ObjectState state = demandFacade.getById(demand.getId()).orElseThrow(() -> ClientException.notFoundError(demandId, Demand.class)).getState();
        demand.setState(state);
        demand = demandFacade.merge(demand);
        JsonObject jsonObject = demandDataConverter.serialize(demand, DataConverter.SerializeView.objectInFabricToJsonToApp);
        RemoteCallClient.notifyObjectAccessed(demandFacade.getChaincodeStub(), user.getId(), demand.getProjectId());
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    @Override
    public void loadHandlers(RequestDependencies<Demand> dependencies) {
        dependencies.addHandler(Offer.class.getSimpleName(), new OfferUpdateHandler(
                dependencies.getChaincodeStub(),
                dependencies.getObjectDependencies().getDataConverterProvider().getDataConverter(Offer.class.getSimpleName()),
                dependencies.getStateManager(),
                dependencies.getObjectFacade()
        ));

        dependencies.addHandler(Contract.class.getSimpleName(), new ContractUpdatedHandler(
                dependencies.getChaincodeStub(),
                dependencies.getObjectDependencies().getDataConverterProvider().getDataConverter(Contract.class.getSimpleName()),
                dependencies.getStateManager(),
                dependencies.getObjectFacade()
        ));
    }

    @Override
    public void registerObjectTypesToBeNotified(ObjectUpdatedNotifier<Demand> objectUpdatedNotifier) {
        objectUpdatedNotifier.addNotifier(ChaincodeName.OFFER_CC);
    }

    public static void main(String[] args) {
        DemandDataConverter dataConverter = new DemandDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Demand> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Demand> objectFacadeFactory = DemandFacade::new;
        StateManager.StateManagerFactory<Demand> stateManagerFactory = StateManager::new;
        ObjectFlow statesFlow = new DemandILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(
                statesFlow.getFlowStepsAsSet(),
                userRoleCheckerFactory
        );
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new DemandAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Demand> actionTriggerServiceFactory = (sm, params) -> new StateActionTriggerService<>(dataConverter, sm, params);

        ObjectDependenciesFactory<Demand> objectDependencies = new ObjectDependenciesFactory<>(
                statesFlow,
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

        new ObjectChaincode(objectDependencies).start(args);
    }
}
