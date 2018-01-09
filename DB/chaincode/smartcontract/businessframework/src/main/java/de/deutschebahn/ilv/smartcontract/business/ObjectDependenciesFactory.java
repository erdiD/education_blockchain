package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ExceptionMapper;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Created by AlbertLacambraBasil on 05.10.2017.
 */
public class ObjectDependenciesFactory<T extends BusinessObject> {

    private final ObjectFlow objectFlow;
    private final DataConverter<T> dataConverter;
    private final ChaincodeTargetFactory chaincodeTargetFactory;
    private final ObjectUpdatedNotifier<T> objectUpdatedNotifier;
    private final DataConverterProvider dataConverterProvider;
    private final ExceptionMapper exceptionMapper;
    private final UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory;
    private final ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory;
    private final UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory;
    private final ObjectFacade.ObjectFacadeFactory<T> objectFacadeFactory;
    private final StateManager.StateManagerFactory<T> stateManagerFactory;
    private final AvailableActionsService availableActionsService;
    private final StateActionTriggerService.StateActionTriggerServiceFactory<T> triggerServiceFactory;


    public ObjectDependenciesFactory(
            ObjectFlow objectFlow,
            DataConverter<T> dataConverter,
            ChaincodeTargetFactory chaincodeTargetFactory,
            ObjectUpdatedNotifier<T> objectUpdatedNotifier,
            DataConverterProvider dataConverterProvider,
            UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory,
            ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory,
            UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory,
            ObjectFacade.ObjectFacadeFactory<T> objectFacadeFactory,
            StateManager.StateManagerFactory<T> stateManagerFactory,
            AvailableActionsService availableActionsService,
            StateActionTriggerService.StateActionTriggerServiceFactory<T> triggerServiceFactory) {

        this.objectFlow = objectFlow;
        this.dataConverter = dataConverter;
        this.chaincodeTargetFactory = chaincodeTargetFactory;
        this.objectUpdatedNotifier = objectUpdatedNotifier;
        this.dataConverterProvider = dataConverterProvider;
        this.userRoleCheckerFactory = userRoleCheckerFactory;
        this.accessServiceFactory = accessServiceFactory;
        this.userPrincipalServiceFactory = userPrincipalServiceFactory;
        this.objectFacadeFactory = objectFacadeFactory;
        this.stateManagerFactory = stateManagerFactory;
        this.availableActionsService = availableActionsService;
        this.triggerServiceFactory = triggerServiceFactory;

        //TODO: put it in the constructor
        exceptionMapper = new ExceptionMapper();
    }

    public ObjectUpdatedNotifier<T> getObjectUpdatedNotifier() {
        return objectUpdatedNotifier;
    }

    public ChaincodeTargetFactory getChaincodeTargetFactory() {
        return chaincodeTargetFactory;
    }

    public ObjectUpdatedHandlerTemplate getObjectUpdatedHandler() {
        return null;
    }

    public ObjectFlow getObjectFlow() {
        return objectFlow;
    }

    public <R extends DataConverter<T>> R getDataConverter() {
        return (R) dataConverter;
    }

    public DataConverterProvider getDataConverterProvider() {
        return dataConverterProvider;
    }

    public ExceptionMapper getExceptionMapper() {
        return exceptionMapper;
    }

    public UserRoleChecker.UserRoleCheckerFactory getUserRoleCheckerFactory() {
        return userRoleCheckerFactory;
    }

    public UserRoleChecker createUserRoleChecker(User u, BusinessObject businessObject) {
        return userRoleCheckerFactory.getUserRoleChecker(u, businessObject);
    }

    public AvailableActionsService getAvailableActionsService() {
        return availableActionsService;
    }

    public UserPrincipalService createUserPrincipalService(ChaincodeStub stub, String userId) {
        return userPrincipalServiceFactory.getUserPrincipalService(stub, dataConverterProvider.getDataConverter(User.class.getSimpleName()), userId);
    }

    public ObjectFacade<T> createObjectFacade(ChaincodeStub stub) {
        return objectFacadeFactory.getObjectFacade(dataConverter, stub);
    }

    public ObjectAccessService<T> createObjectAccessService(ChaincodeStub stub) {
        return accessServiceFactory.getObjectAccessService(stub);
    }

    public StateActionTriggerService<T> createActionTriggerService(StateManager<T> stateManager, List<String> params) {
        return triggerServiceFactory.getStateActionTriggerService(stateManager, params);
    }

    public StateManager<T> createStateManager(ChaincodeStub stub) {
        return stateManagerFactory.getStateManager(objectFlow, createObjectFacade(stub), objectUpdatedNotifier, stub, getUserRoleCheckerFactory());
    }
}
