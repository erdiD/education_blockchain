package de.deutschebahn.ilv.smartcontract.offer;

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
public class OfferUpdatedHandler extends ObjectUpdatedHandlerTemplate<Offer> {

    private static final Logger logger = Logger.getLogger(OfferUpdatedHandler.class.getName());
    private final StateManager<Offer> offerStateManager;
    private final PeerRoleChecker peerRoleChecker;
    private final OfferFacade offerFacade;

    public OfferUpdatedHandler(StateManager<Offer> offerStateManager,
                               PeerRoleChecker peerRoleChecker,
                               ObjectFacade<Offer> offerFacade,
                               ChaincodeStub chaincodeStub,
                               DataConverter<Offer> dataConverter) {

        super(chaincodeStub, dataConverter);
        this.offerStateManager = offerStateManager;
        this.peerRoleChecker = peerRoleChecker;
        this.offerFacade = (OfferFacade) offerFacade;
    }

    @Override
    public void handle(Offer object) {
        logger.info("[handle] Received OfferUpdated notification. # Offer=" + object);

        if (object.getState() == ObjectState.OFFER_COMPLETED) {
            List<Offer> offers = offerFacade.findAll(object.getProjectId());
            offers.stream()
                    .filter(offer -> !object.equals(offer))
                    .forEach(offer -> offerStateManager.peerTriggerOption(offer, ObjectStateTransitionAction.CLOSE_OFFER));
        }
    }
}