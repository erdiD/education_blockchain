package de.deutschebahn.ilv.smartcontract.offer;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.authorization.PeerRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public class ContractUpdatedHandler extends ObjectUpdatedHandlerTemplate<Contract> {

    private static final Logger logger = Logger.getLogger(ContractUpdatedHandler.class.getName());
    private final StateManager<Offer> offerStateManager;
    private final PeerRoleChecker peerRoleChecker;
    private final ObjectFacade<Offer> offerFacade;

    public ContractUpdatedHandler(StateManager<Offer> offerStateManager,
                                  PeerRoleChecker peerRoleChecker,
                                  ObjectFacade<Offer> offerFacade,
                                  ChaincodeStub chaincodeStub,
                                  DataConverter<Contract> dataConverter) {

        super(chaincodeStub, dataConverter);
        this.offerStateManager = offerStateManager;
        this.peerRoleChecker = peerRoleChecker;
        this.offerFacade = offerFacade;
    }

    @Override
    public void handle(Contract object) {
        if (object.getState() == ObjectState.CONTRACT_SIGNED) {
            offerStateManager.peerTriggerOption(object.getOfferId(), ObjectStateTransitionAction.LOCK_OFFER);
        }
    }
}