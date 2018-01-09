package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

/**
 * Created by AlbertLacambraBasil on 13.06.2017.
 */
public class FlowStep {
    private MarketRoleName role;
    private ObjectState currentState;
    private ObjectStateTransitionAction stateTransitionAction;
    private ObjectState nextState;

    /**
     * Describes an state transition
     *
     * @param currentState:          represents the current state of the BO
     * @param stateTransitionAction: action that needs to be triggered in order to change BO state
     * @param nextState:             state where the BO will be once the transition has successfully completed
     * @param role:                  the organization role allowed to perform this transition
     */
    public FlowStep(ObjectState currentState, ObjectStateTransitionAction stateTransitionAction, ObjectState nextState, MarketRoleName role) {
        this.role = role;
        this.currentState = currentState;
        this.stateTransitionAction = stateTransitionAction;
        this.nextState = nextState;
    }

    public MarketRoleName getRole() {
        return role;
    }

    public ObjectState getCurrentState() {
        return currentState;
    }

    public ObjectStateTransitionAction getStateTransitionAction() {
        return stateTransitionAction;
    }

    public ObjectState getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "FlowStep{" +
                "role=" + role +
                ", currentState=" + currentState +
                ", stateTransitionAction=" + stateTransitionAction +
                ", nextState=" + nextState +
                '}';
    }
}
