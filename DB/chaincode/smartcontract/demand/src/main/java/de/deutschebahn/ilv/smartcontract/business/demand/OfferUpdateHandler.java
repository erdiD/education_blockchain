package de.deutschebahn.ilv.smartcontract.business.demand;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class OfferUpdateHandler extends ObjectUpdatedHandlerTemplate<Offer> {
    private static final Logger logger = Logger.getLogger(OfferUpdateHandler.class.getName());
    private final StateManager<Demand> stateManager;
    private final ObjectFacade<Demand> objectFacade;

    public OfferUpdateHandler(ChaincodeStub chaincodeStub,
                              DataConverter<Offer> dataConverter,
                              StateManager<Demand> stateManager,
                              ObjectFacade<Demand> objectFacade) {

        super(chaincodeStub, dataConverter);
        this.stateManager = stateManager;
        this.objectFacade = objectFacade;
    }

    @Override
    public void handle(Offer object) {
        String demandId = IdUtils.recreateDemandId(object.getProjectId());
        Demand demand = objectFacade.getById(demandId).orElseThrow(() -> new RuntimeException());
        logger.info(String.format("[handle] Update received # sourceId=%s, sourceState=%s", object.getId(), object.getState()));

        switch (object.getState()) {
            case OFFER_COMPLETED:
            case OFFER_APPROVED:
                stateManager.peerTriggerOption(demand, ObjectStateTransitionAction.COMPLETE_DEMAND);
                break;
            case OFFER_OFFERED:
            case OFFER_NOT_CREATED:
            case OFFER_OPENED:
            case OFFER_WAITING:
            case OFFER_ACCEPTED:
            case OFFER_EXPIRED:
            case OFFER_REJECTED:
            case OFFER_CLOSED:
            default:
        }
    }
}
