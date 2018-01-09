package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class ObjectFlow {

    private final Map<ObjectStateTransitionAction, Set<FlowStep>> flows;

    protected ObjectFlow() {
        flows = new HashMap<>();
    }

    public Map<ObjectStateTransitionAction, Set<FlowStep>> getFlowSteps() {
        return flows;
    }

    protected void addFlowStep(ObjectState currentState,
                               ObjectStateTransitionAction stateTransitionAction,
                               ObjectState nextState,
                               MarketRoleName... roles) {

        addFlowStep(currentState, stateTransitionAction, nextState, Arrays.asList(roles));
    }

    protected void addFlowStep(ObjectState currentState,
                               ObjectStateTransitionAction stateTransitionAction,
                               ObjectState nextState,
                               Collection<MarketRoleName> roles) {

        for (MarketRoleName role : roles) {
            FlowStep flowStep = new FlowStep(
                    currentState,
                    stateTransitionAction,
                    nextState,
                    role
            );

            if (!flows.containsKey(stateTransitionAction)) {
                flows.put(stateTransitionAction, new HashSet<>());
            }

            flows.get(stateTransitionAction).add(flowStep);
        }
    }

    public FlowBuilder fromState(ObjectState sourceState) {
        return new FlowBuilder(sourceState);
    }

    public FlowBuilder fromStateAndRole(ObjectState sourceState, MarketRoleName role) {
        return new FlowBuilder(sourceState, role);
    }

    public Set<FlowStep> getFlowStepsAsSet() {
        return flows.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }


    public class FlowBuilder {
        private final ObjectState currentState;
        private final boolean roleIsFixed;

        private FlowBuilder(ObjectState currentState) {
            this.currentState = currentState;
            roleIsFixed = false;
        }

        ObjectStateTransitionAction stateTransitionAction;
        ObjectState nextState;
        Set<MarketRoleName> roles = new HashSet<>();

        private FlowBuilder(ObjectState currentState, MarketRoleName role) {
            this.currentState = currentState;
            this.roles.add(role);
            roleIsFixed = true;
        }

        public FlowBuilder goToState(ObjectState nextState) {
            if (this.nextState != null) {
                throw new RuntimeException("nextState already set");
            }
            this.nextState = nextState;
            return this;
        }

        public FlowBuilder whenRole(MarketRoleName roleName) {
            if (roleIsFixed) {
                throw new RuntimeException("Not Possible to add roles. Fixed to " + roles);
            }
            this.roles.add(roleName);
            return this;
        }

        public FlowBuilder triggers(ObjectStateTransitionAction stateTransitionAction) {
            if (this.stateTransitionAction != null) {
                throw new RuntimeException("action already set");
            }
            this.stateTransitionAction = stateTransitionAction;
            return this;
        }

        public FlowBuilder whenTriggers(ObjectStateTransitionAction stateTransitionAction) {
            return triggers(stateTransitionAction);
        }

        public void build() {
            addFlowStep(currentState, stateTransitionAction, nextState, roles);
        }

        public FlowBuilder or() {
            build();
            if (roleIsFixed) {
                return new FlowBuilder(currentState, roles.iterator().next());
            } else {
                return new FlowBuilder(currentState);
            }
        }

        public FlowBuilder orWhenRole(MarketRoleName role) {
            build();
            return new FlowBuilder(currentState, role);
        }
    }
}
