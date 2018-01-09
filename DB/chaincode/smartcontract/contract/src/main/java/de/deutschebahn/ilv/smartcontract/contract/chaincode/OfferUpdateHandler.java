package de.deutschebahn.ilv.smartcontract.contract.chaincode;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.business.ChaincodeName;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 04.07.2017.
 */
public class OfferUpdateHandler extends ObjectUpdatedHandlerTemplate<Offer> {
    private static final Logger logger = Logger.getLogger(OfferUpdateHandler.class.getName());
    private final StateManager<Contract> stateManager;
    private final ObjectFacade<Contract> objectFacade;

    public OfferUpdateHandler(ChaincodeStub chaincodeStub,
                              DataConverter<Offer> dataConverter,
                              StateManager<Contract> stateManager,
                              ObjectFacade<Contract> objectFacade) {

        super(chaincodeStub, dataConverter);
        this.stateManager = stateManager;
        this.objectFacade = objectFacade;
    }

    @Override
    public void handle(Offer offer) {
        logger.info(String.format("[handle] Offer event received. # cc=%s, offer=%s", ChaincodeName.CONTRACT_CC, offer));

        if (offer.getState() == ObjectState.OFFER_COMPLETED) {

            Contract contract = new Contract();
            contract.setId(IdUtils.recreateContractId(offer.getProjectId()));
            logger.info(String.format("[handle] Received completed offer. Creating contract # OfferId=%s, ContractId=%s", offer.getId(), contract.getId()));
            contract.setState(ObjectState.CONTRACT_NOT_CREATED);
            contract.setOfferId(offer.getId());
            contract.setProjectId(offer.getProjectId());
            contract.setContractType(offer.getContractType());
            contract.setStartDateDate(offer.getStartDate());
            contract.setDeliveryDate(offer.getDeliveryDate());
            contract.setPaymentType(offer.getPaymentType());

            //TODO: budget should come with the offer or as parameter
            BigDecimal budget = RemoteCallClient.getProjectField(
                    getChaincodeStub()
                    //TODO: recheck that!
                    , offer.getCreatorId()
                    , offer.getProjectId()
                    , "demandBudget"
            ).map(SerializationHelper::convertToBigDecimal).orElse(BigDecimal.ONE);
            contract.setBudget(budget);

            try {
                stateManager.peerTriggerOption(contract, ObjectStateTransitionAction.CREATE_CONTRACT);
            } catch (Exception e) {
                logger.warning("[handle] " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            logger.info("[handle] contract created # contractId=" + contract.getId() + ", offerId" + offer.getId());
        }
    }
}
