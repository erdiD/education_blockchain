package de.deutschebahn.ilv.smartcontract.contract.chaincode;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;

import static de.deutschebahn.ilv.domain.MarketRoleName.*;
import static de.deutschebahn.ilv.domain.ObjectState.*;
import static de.deutschebahn.ilv.domain.ObjectStateTransitionAction.*;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class ContractILVFlow extends ObjectFlow {

    public ContractILVFlow() {

        fromState(CONTRACT_NOT_CREATED)
                .whenRole(MarketRoleName.PEER).triggers(CREATE_CONTRACT).goToState(CONTRACT_CREATED)
                .build();

        fromStateAndRole(CONTRACT_CREATED, CUSTOMER_SIGNER)
                .triggers(SIGN_CONTRACT).goToState(CONTRACT_CLIENT_SIGNED).or()
                .whenTriggers(REJECT_CONTRACT).goToState(CONTRACT_REJECTED)
                .orWhenRole(SUPPLIER_SIGNER)
                .triggers(SIGN_CONTRACT).goToState(CONTRACT_SUPPLIER_SIGNED).or()
                .whenTriggers(REJECT_CONTRACT).goToState(CONTRACT_REJECTED)
                .build();

        fromStateAndRole(ObjectState.CONTRACT_CLIENT_SIGNED, SUPPLIER_SIGNER)
                .triggers(SIGN_CONTRACT).goToState(CONTRACT_SIGNED).or()
                .whenTriggers(REJECT_CONTRACT).goToState(CONTRACT_REJECTED)
                .build();

        fromStateAndRole(ObjectState.CONTRACT_SUPPLIER_SIGNED, CUSTOMER_SIGNER)
                .triggers(SIGN_CONTRACT).goToState(CONTRACT_SIGNED).or()
                .whenTriggers(REJECT_CONTRACT).goToState(CONTRACT_REJECTED)
                .build();

        fromStateAndRole(ObjectState.CONTRACT_SIGNED, DEMAND_CREATOR)
                .triggers(TERMINATE_CONTRACT).goToState(CONTRACT_TERMINATED)
                .build();

    }
}