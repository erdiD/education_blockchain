package de.deutschebahn.ilv.smartcontract.delivery.chaincode;

import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;

import static de.deutschebahn.ilv.domain.MarketRoleName.*;
import static de.deutschebahn.ilv.domain.ObjectState.*;
import static de.deutschebahn.ilv.domain.ObjectStateTransitionAction.*;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class DeliveryILVFlow extends ObjectFlow {

    public DeliveryILVFlow() {

        fromStateAndRole(DELIVERY_NOT_CREATED, PEER)
                .goToState(DELIVERY_CREATED).whenTriggers(CREATE_DELIVERY).or()
                .goToState(DELIVERY_CLOSED).whenTriggers(CLOSE_DELIVERY)
                .build();

        fromState(DELIVERY_CREATED)
                .goToState(DELIVERY_ACTIVE).whenRole(PEER).triggers(ACTIVATE_DELIVERY).or()
                .goToState(DELIVERY_CLOSED).whenRole(PEER).triggers(CLOSE_DELIVERY).or()
                .goToState(DELIVERY_ACTIVE).whenRole(DEMAND_CREATOR).triggers(CLOSE_DELIVERY)
                .build();

        fromState(DELIVERY_ACTIVE)
                .goToState(DELIVERY_COMPLETE).whenRole(PEER).triggers(COMPLETE_DELIVERY).or()
                .goToState(DELIVERY_ACTIVE).whenRole(SUPPLIER_PROJECT_MANAGER).triggers(CREATE_DELIVERY_ENTRY).or()
                //TODO: Change it by ADD_DELIVERY_PSP
                .goToState(DELIVERY_ACTIVE).whenRole(SUPPLIER_PROJECT_MANAGER).triggers(UPDATE).or()
                .goToState(DELIVERY_CLOSED).whenRole(PEER).triggers(CLOSE_DELIVERY).or()
                .goToState(DELIVERY_CLOSED).whenRole(DEMAND_CREATOR).triggers(CLOSE_DELIVERY)
                .build();
    }
}