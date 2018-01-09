package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.InvalidActionException;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.authorization.PeerRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.authorization.RoleChecker;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.commons.ActionPerformedEvent;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeEvent;
import de.deutschebahn.ilv.smartcontract.commons.ClientException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * it manage the transitions between states of a demand. It is responsible to notify this transitions to the system,
 * so that other managers can react on it, as well as to manage notifications from other managers
 */
public class StateManager<T extends BusinessObject> {

    private static final Logger logger = Logger.getLogger(StateManager.class.getName());
    private final Map<ObjectStateTransitionAction, Set<FlowStep>> flows;
    private final ObjectFacade<T> facade;
    private final ObjectUpdatedNotifier<T> notifier;
    private final ChaincodeStub chaincodeStub;
    private final UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory;


    public StateManager(ObjectFlow objectFlow, ObjectFacade<T> facade, ObjectUpdatedNotifier notifier, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {

        Objects.requireNonNull(objectFlow);
        Objects.requireNonNull(facade);
        Objects.requireNonNull(notifier);
        Objects.requireNonNull(chaincodeStub);
        Objects.requireNonNull(userRoleCheckerFactory);

        this.flows = objectFlow.getFlowSteps();
        this.facade = facade;
        this.notifier = notifier;
        this.chaincodeStub = chaincodeStub;
        this.userRoleCheckerFactory = userRoleCheckerFactory;
    }

    public T actionTriggered(User user, String businessObjectId, ObjectStateTransitionAction action) {
        logger.info("[actionTriggered] action triggered:" + action + ", user=" + user + ", objectId=" + businessObjectId);
        T businessObject = facade.getById(businessObjectId).orElseThrow(() -> ClientException.notFoundError(businessObjectId, ""));
        businessObject = triggerOption(businessObject, action, userRoleCheckerFactory.getUserRoleChecker(user, businessObject), user);
        return businessObject;
    }

    public T peerTriggerOption(String businessObjectId, ObjectStateTransitionAction action) {
        T object = facade.getById(businessObjectId).orElseThrow(() -> new RuntimeException("Object with found for id=" + businessObjectId));
        return triggerOption(object, action, new PeerRoleChecker(), null);
    }

    public T actionTriggered(User user, T businessObject, ObjectStateTransitionAction action) {
        logger.info("[actionTriggered] action triggered:" + action + ", user=" + user + ", objectId=" + businessObject);
        businessObject = triggerOption(businessObject, action, userRoleCheckerFactory.getUserRoleChecker(user, businessObject), user);
        return businessObject;
    }

    public T peerTriggerOption(T businessObject, ObjectStateTransitionAction action) {
        return triggerOption(businessObject, action, new PeerRoleChecker(), null);
    }

    public T triggerOption(T businessObject, ObjectStateTransitionAction action, RoleChecker roleChecker, User user) {

        ObjectState oldState = businessObject.getState();
        logger.info("[triggerOption] action triggered # action=" + action + ",  object=" + businessObject + ", user=" + user);
        Set<FlowStep> flowSteps = getAcceptableActionFlow(
                action,
                businessObject.getId(),
                businessObject.getState(),
                businessObject.getClass()
        );

        FlowStep flowStep = checkAndFetchCorrectAction(
                roleChecker,
                flowSteps,
                action,
                businessObject.getId(),
                businessObject.getState(),
                businessObject.getClass());

        logger.info("[triggerOption] Flow loaded= " + flowStep);

        T persistedObject = updateObject(flowStep, businessObject);
        logger.info("[triggerOption] State changed. BO=" + businessObject + ", flowStep=" + flowStep);

        HistoryEntry historyEntry = createHistoryEntry(businessObject, user, persistedObject.getState(), oldState, flowStep.getRole(), action);
        sendActionEvent(new ActionPerformedEvent(historyEntry));
        return persistedObject;
    }

    //TODO: extract this methods to a service and replace Unicode key symbols or put a general listener on the client level and set a typical patter observer there
    private void sendActionEvent(ActionPerformedEvent actionPerformedEvent) {
        ChaincodeEvent chaincodeEvent = new ChaincodeEvent(ActionPerformedEvent.NAME, actionPerformedEvent.toJson());
        chaincodeStub.setEvent(chaincodeEvent.getName(), chaincodeEvent.toJson().toString().getBytes());
    }

    private HistoryEntry createHistoryEntry(BusinessObject businessObject, User user, ObjectState newState, ObjectState oldState, MarketRoleName role, ObjectStateTransitionAction action) {
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setCreationTime(businessObject.getLastModified());
        historyEntry.setUser(user);
        historyEntry.setNewState(newState);
        historyEntry.setOldState(oldState);
        historyEntry.setMarketRole(role);
        historyEntry.setAction(action);
        historyEntry.setObjectId(businessObject.getId());
        historyEntry.setProjectId(businessObject.getProjectId());
        return historyEntry;
    }

    private Set<FlowStep> getAcceptableActionFlow(ObjectStateTransitionAction action, String objectId, ObjectState state, Class<?> clazz) {
        Collection<FlowStep> flowSteps = flows.get(action);

        if (flowSteps == null || flowSteps.isEmpty()) {
            logger.info("[loadActionFlows] action not found. Actions=" + action);
            throw InvalidActionException.createActionNotAcceptableException(
                    action.name(),
                    state.name(),
                    clazz,
                    objectId
            );
        }

        Set<FlowStep> filteredFlowStepsPerAction = flowSteps
                .stream()
                .filter(af -> af.getCurrentState() == state)
                .collect(toSet());

        if (filteredFlowStepsPerAction.isEmpty()) {
            logger.info("[getAcceptableActionFlow] action not found for current object state. Actions=" + action
                    + ", current object state=" + state + ", objectId=" + objectId);

            throw InvalidActionException.createActionNotAcceptableException(action.name(), state.name(), clazz, objectId);
        }

        return filteredFlowStepsPerAction;
    }


    public FlowStep checkAndFetchCorrectAction(RoleChecker roleChecker,
                                               Set<FlowStep> availableFlowSteps,
                                               ObjectStateTransitionAction action,
                                               String objectId,
                                               ObjectState state,
                                               Class<?> clazz) {
        Set<FlowStep> filteredFlowStepsPerRole = availableFlowSteps
                .stream()
                .filter(roleChecker::hasRequiredRoleToRunActionFlow)
                .collect(toSet());

        if (!availableFlowSteps.isEmpty() && filteredFlowStepsPerRole.isEmpty()) {
            logger.info("[getAcceptableActionFlow] no valid role for action: " + action + ", " + clazz.getSimpleName() + "="
                    + objectId + ", state=" + state + ", flows for action=" + availableFlowSteps);

            throw InvalidActionException.createNotAcceptableRolesException(
                    action.name(),
                    state.name(),
                    clazz,
                    objectId);
        }

        if (filteredFlowStepsPerRole.size() > 1) {
            logger.warning("[checkAndFetchCorrectAction] too many flows! flows=" + filteredFlowStepsPerRole);
            throw new RuntimeException("Not possible to have more than to states transitions. Found=" + filteredFlowStepsPerRole);
        }

        return filteredFlowStepsPerRole.stream().findFirst().get();
    }

    protected T updateObject(FlowStep action, T businessObject) {

        ObjectState nextState = action.getNextState();
        businessObject.setState(nextState);
        businessObject.addAccessRole(action.getRole());

        T persistedBusinessObject = facade.merge(businessObject);
        notifier.notifyUpdate(chaincodeStub, persistedBusinessObject);
        logger.info("[updateObject] State changed. Object=" + businessObject + ", actionFlow=" + action);

        return persistedBusinessObject;
    }

    protected ObjectFacade<T> getObjectFacade() {
        return null;
    }

    public Stream<FlowStep> getFlows() {
        return flows.values()
                .stream()
                .flatMap(Collection::stream);
    }

    public ChaincodeStub getChaincodeStub() {
        return chaincodeStub;
    }

    public interface StateManagerFactory<T extends BusinessObject> {
        StateManager<T> getStateManager(ObjectFlow objectFlow, ObjectFacade<T> facade, ObjectUpdatedNotifier notifier, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory);
    }

}