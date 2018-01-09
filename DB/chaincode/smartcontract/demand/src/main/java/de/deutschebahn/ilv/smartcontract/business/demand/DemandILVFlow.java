package de.deutschebahn.ilv.smartcontract.business.demand;

import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;

import static de.deutschebahn.ilv.domain.MarketRoleName.DEMAND_CREATOR;
import static de.deutschebahn.ilv.domain.MarketRoleName.PEER;
import static de.deutschebahn.ilv.domain.ObjectState.*;
import static de.deutschebahn.ilv.domain.ObjectStateTransitionAction.*;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class DemandILVFlow extends ObjectFlow {

    public DemandILVFlow() {
        fromStateAndRole(NOT_CREATED, DEMAND_CREATOR)
                .goToState(DEMAND_OPENED).whenTriggers(CREATE_DEMAND)
                .build();

        fromStateAndRole(DEMAND_OPENED, DEMAND_CREATOR)
                .goToState(DEMAND_SUBMITTED).whenTriggers(SUBMIT_DEMAND).or()
                .goToState(DEMAND_CLOSED).whenTriggers(CLOSE_DEMAND).or()
                .goToState(DEMAND_OPENED).whenTriggers(UPDATE)
                .build();

        fromState(DEMAND_SUBMITTED)
                .goToState(DEMAND_CLOSED).whenRole(DEMAND_CREATOR).triggers(RESIGN_DEMAND).or()
                .whenRole(PEER).triggers(COMPLETE_DEMAND).goToState(DEMAND_COMPLETED)
                .build();

        fromState(DEMAND_COMPLETED)
                .whenRole(DEMAND_CREATOR).triggers(RESIGN_DEMAND).goToState(DEMAND_CLOSED)
                .orWhenRole(PEER).triggers(LOCK_DEMAND).goToState(DEMAND_LOCKED)
                .build();
    }
}