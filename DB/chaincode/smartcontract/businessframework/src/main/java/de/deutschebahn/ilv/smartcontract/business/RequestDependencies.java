package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public class RequestDependencies<T extends BusinessObject> {

    private final ChaincodeStub chaincodeStub;
    private final ObjectDependenciesFactory<T> objectDependencies;
    private final DataConverterProvider dataConverterProvider;
    private final Map<String, ObjectUpdatedHandlerTemplate<?>> handlers;
    private final ObjectFlow objectFlow;
    private final UserPrincipalService userPrincipalService;
    private final ObjectFacade<T> facade;
    private final List<String> params;
    private final ObjectAccessService<T> objectAccessService;
    private final GenericActions action;
    private final User principal;
    private final StateActionTriggerService<T> actionTriggerService;

    public RequestDependencies(ChaincodeStub chaincodeStub,
                               ObjectDependenciesFactory<T> objectDependencies,
                               UserPrincipalService userPrincipalService,
                               ObjectFacade<T> facade,
                               ObjectAccessService<T> objectAccessService,
                               List<String> params,
                               User principal) {

        this.chaincodeStub = chaincodeStub;
        this.facade = facade;
        this.objectDependencies = objectDependencies;
        this.dataConverterProvider = objectDependencies.getDataConverterProvider();
        this.objectFlow = objectDependencies.getObjectFlow();
        this.userPrincipalService = userPrincipalService;
        this.objectAccessService = objectAccessService;
        this.params = params;
        this.principal = principal;
        this.actionTriggerService = objectDependencies.createActionTriggerService(getStateManager(), params);
        handlers = new HashMap<>();
        action = GenericActions.valueOf(chaincodeStub.getFunction());
    }

    public ChaincodeStub getChaincodeStub() {
        return chaincodeStub;
    }

    public <H extends ObjectUpdatedHandlerTemplate> Optional<H> getHandler(String objectType) {
        return Optional.ofNullable((H) handlers.get(objectType));
    }

    public <F extends ObjectFacade<T>> F getObjectFacade() {
        return (F) facade;
    }

    public StateManager<T> getStateManager() {
        return new StateManager<T>(objectFlow, facade, objectDependencies.getObjectUpdatedNotifier(), chaincodeStub, objectDependencies.getUserRoleCheckerFactory());
    }

    public RequestDependencies<T> addHandler(String objectType, ObjectUpdatedHandlerTemplate<?> objectUpdatedHandlerTemplate) {
        handlers.put(objectType, objectUpdatedHandlerTemplate);
        return this;
    }

    public ObjectDependenciesFactory<T> getObjectDependencies() {
        return objectDependencies;
    }

    public <R> DataConverter<R> getDataConverterForObjectType(Class<R> clazz) {
        return objectDependencies.getDataConverterProvider().getDataConverter(clazz.getSimpleName());
    }

    public AvailableActionsService getAvailableActionsService() {
        return objectDependencies.getAvailableActionsService();
    }

    public UserPrincipalService getUserPrincipalService() {
        return userPrincipalService;
    }

    public User getPrincipal() {
        return principal;
    }

    public <S extends ObjectAccessService<T>> S getObjectAccessService() {
        return (S) objectAccessService;
    }

    public List<String> getParams() {
        return params;
    }

    public GenericActions getAction() {
        return action;
    }

    public StateActionTriggerService<T> getActionTriggerService() {
        return actionTriggerService;
    }
}