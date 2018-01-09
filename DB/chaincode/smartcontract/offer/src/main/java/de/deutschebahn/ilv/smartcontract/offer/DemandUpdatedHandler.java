package de.deutschebahn.ilv.smartcontract.offer;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.authorization.PeerRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public class DemandUpdatedHandler extends ObjectUpdatedHandlerTemplate<Demand> {

    private static final Logger logger = Logger.getLogger(DemandUpdatedHandler.class.getName());
    private final StateManager<Offer> offerStateManager;
    private final PeerRoleChecker peerRoleChecker;
    private final OfferFacade offerFacade;

    public DemandUpdatedHandler(StateManager<Offer> offerStateManager,
                                PeerRoleChecker peerRoleChecker,
                                ObjectFacade<Offer> offerFacade,
                                ChaincodeStub chaincodeStub,
                                DataConverter<Demand> dataConverter) {

        super(chaincodeStub, dataConverter);
        this.offerStateManager = offerStateManager;
        this.peerRoleChecker = peerRoleChecker;
        this.offerFacade = (OfferFacade) offerFacade;
    }

    @Override
    public void handle(Demand object) {
        logger.info(String.format("[handle] Received updated demand event # demandState=%s, demand=%s", object.getState(), object));

        if (object.getState() == ObjectState.DEMAND_CLOSED) {
            List<Offer> offers = offerFacade.findAll(object.getProjectId());
            offers.forEach(offer -> offerStateManager.peerTriggerOption(offer, ObjectStateTransitionAction.CLOSE_OFFER));
        }
    }
}