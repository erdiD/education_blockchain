package de.deutschebahn.ilv.smartcontract.offer;

import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;

import static de.deutschebahn.ilv.domain.MarketRoleName.*;
import static de.deutschebahn.ilv.domain.ObjectState.*;
import static de.deutschebahn.ilv.domain.ObjectStateTransitionAction.*;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class OfferILVFlow extends ObjectFlow {

    public OfferILVFlow() {
        fromState(NOT_CREATED)
                .goToState(OFFER_OPENED).whenRole(OFFER_CREATOR).triggers(OPEN_OFFER).build();

        fromStateAndRole(OFFER_OPENED, OFFER_CREATOR)
                .goToState(OFFER_WAITING).whenTriggers(REVIEW_OFFER).or()
                .goToState(OFFER_OPENED).whenTriggers(UPDATE).or()
                .goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();

        fromStateAndRole(OFFER_WAITING, SUPPLIER_OFFER_APPROVAL)
                .goToState(OFFER_CLOSED).whenTriggers(REVOKE_OFFER).or()
                .goToState(OFFER_OFFERED).whenTriggers(APPROVE_OFFER_INTERNAL)
                .orWhenRole(OFFER_CREATOR).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();

        fromStateAndRole(OFFER_OFFERED, DEMAND_CREATOR)
                .goToState(OFFER_CLOSED).whenTriggers(REJECT_OFFER).or()
                .goToState(OFFER_ACCEPTED).whenTriggers(ACCEPT_OFFER)
                .orWhenRole(OFFER_CREATOR).goToState(OFFER_CLOSED).whenTriggers(RESIGN_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();


        fromStateAndRole(OFFER_ACCEPTED, CUSTOMER_OFFER_COMMERCIAL_APPROVAL)
                .goToState(OFFER_CLOSED).whenTriggers(REJECT_OFFER).or()
                .goToState(OFFER_COMM_APPROVED).whenTriggers(APPROVE_OFFER)
                .orWhenRole(CUSTOMER_OFFER_TECHNICAL_APPROVAL)
                .whenTriggers(REJECT_OFFER).goToState(OFFER_CLOSED).or()
                .whenTriggers(APPROVE_OFFER).goToState(OFFER_TECH_APPROVED)
                .orWhenRole(OFFER_CREATOR)
                .whenTriggers(RESIGN_OFFER).goToState(OFFER_CLOSED)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();


        fromStateAndRole(OFFER_COMM_APPROVED, CUSTOMER_OFFER_TECHNICAL_APPROVAL)
                .goToState(OFFER_CLOSED).whenTriggers(REJECT_OFFER).or()
                .goToState(OFFER_COMPLETED).whenTriggers(APPROVE_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();

        fromStateAndRole(OFFER_TECH_APPROVED, CUSTOMER_OFFER_COMMERCIAL_APPROVAL)
                .goToState(OFFER_CLOSED).whenTriggers(REJECT_OFFER).or()
                .goToState(OFFER_COMPLETED).whenTriggers(APPROVE_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .build();

        fromStateAndRole(OFFER_COMPLETED, OFFER_CREATOR)
                .goToState(OFFER_CLOSED).whenTriggers(RESIGN_OFFER)
                .orWhenRole(PEER).goToState(OFFER_CLOSED).whenTriggers(CLOSE_OFFER)
                .or().goToState(OFFER_LOCKED).whenTriggers(LOCK_OFFER)
                .build();
    }
}
