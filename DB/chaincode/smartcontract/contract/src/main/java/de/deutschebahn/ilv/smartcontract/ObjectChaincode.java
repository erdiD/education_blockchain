package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.contract.chaincode.ContractAccessService;
import de.deutschebahn.ilv.smartcontract.contract.chaincode.ContractFacade;
import de.deutschebahn.ilv.smartcontract.contract.chaincode.ContractILVFlow;
import de.deutschebahn.ilv.smartcontract.contract.chaincode.OfferUpdateHandler;

import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ObjectChaincode extends ChaincodeInvocation<Contract> {

    private Logger logger = Logger.getLogger(ObjectChaincode.class.getName());

    private ContractDataConverter contractDataConverter;
    private DataConverterProvider dataConverterProvider;

    public ObjectChaincode(ObjectDependenciesFactory<Contract> objectDependencies) {
        super(objectDependencies);
        contractDataConverter = objectDependencies.getDataConverter();
        dataConverterProvider = objectDependencies.getDataConverterProvider();
    }

    @Override
    protected ChaincodeResponseMessage invoke(GenericActions action, boolean isUserAction, RequestDependencies<Contract> requestDependencies) {
        ContractFacade contractFacade = requestDependencies.getObjectFacade();
        ContractAccessService contractAccessService = requestDependencies.getObjectAccessService();
        User principal = requestDependencies.getPrincipal();
        List<String> params = requestDependencies.getParams();
        StateActionTriggerService<Contract> actionTriggerService = requestDependencies.getActionTriggerService();
        AvailableActionsService availableActionsService = requestDependencies.getAvailableActionsService();
        ChaincodeResponseMessage response;

        switch (action) {
            case getById:
                response = getById(principal, contractAccessService, contractFacade, availableActionsService, params);
                break;
            case fireAction:
                if (!isUserAction) {
                    response = actionTriggerService.fireActionAsPeer();
                } else {
                    response = actionTriggerService.fireActionAsPrincipal(principal);
                }
                break;
            default:
                response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
                break;
        }
        return response;
    }

    private ChaincodeResponseMessage getById(User user, ContractAccessService contractAccessService, ContractFacade contractFacade,
                                             AvailableActionsService availableActionsService, List<String> params) {
        String projectId = params.get(0);
        projectId = IdUtils.getProjectId(IdUtils.stringToCompositeKey(projectId));
        String contractId = IdUtils.recreateContractId(projectId);
        Contract contract = contractFacade.getById(contractId).orElseThrow(() -> ClientException.notFoundError(contractId, Contract.class));
        if (!contractAccessService.canRead(user, contract)) {
            throw ClientException.notAuthorized();
        }
        contract = contractFacade.prepareObject(contract, availableActionsService, user);
        JsonObject jsonObject = contractDataConverter.serialize(contract, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    @Override
    public void registerObjectTypesToBeNotified(ObjectUpdatedNotifier<Contract> objectUpdatedNotifier) {
        objectUpdatedNotifier.addNotifier(ChaincodeName.OFFER_CC);
        objectUpdatedNotifier.addNotifier(ChaincodeName.DEMAND_CC);
        objectUpdatedNotifier.addNotifier(ChaincodeName.DELIVERY_CC);
    }

    @Override
    public void loadHandlers(RequestDependencies<Contract> dependencies) {
        dependencies.addHandler(Offer.class.getSimpleName(), new OfferUpdateHandler(
                dependencies.getChaincodeStub(),
                dataConverterProvider.getDataConverter(Offer.class.getSimpleName()),
                dependencies.getStateManager(),
                dependencies.getObjectFacade()
        ));
    }

    public static void main(String[] args) {

        ContractDataConverter dataConverter = new ContractDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Contract> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Contract> objectFacadeFactory = ContractFacade::new;
        StateManager.StateManagerFactory<Contract> stateManagerFactory = StateManager::new;
        ObjectFlow objectFlow = new ContractILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(objectFlow.getFlowStepsAsSet(), userRoleCheckerFactory);
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new ContractAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Contract> triggerServiceFactory =
                (stateManager, params) -> new StateActionTriggerService(dataConverter, stateManager, params);

        ObjectDependenciesFactory<Contract> dependencies = new ObjectDependenciesFactory<>(
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
                triggerServiceFactory
        );

        new ObjectChaincode(dependencies).start(args);
    }
}