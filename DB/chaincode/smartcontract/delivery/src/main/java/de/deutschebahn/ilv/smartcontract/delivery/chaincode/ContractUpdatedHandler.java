package de.deutschebahn.ilv.smartcontract.delivery.chaincode;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public class ContractUpdatedHandler extends ObjectUpdatedHandlerTemplate<Contract> {

    private static final Logger logger = Logger.getLogger(ContractUpdatedHandler.class.getName());
    private final StateManager<Delivery> stateManager;
    private final DeliveryFacade deliveryFacade;

    public ContractUpdatedHandler(StateManager<Delivery> offerStateManager,
                                  ObjectFacade<Delivery> deliveryFacade,
                                  ChaincodeStub chaincodeStub,
                                  DataConverter<Contract> dataConverter) {

        super(chaincodeStub, dataConverter);
        this.stateManager = offerStateManager;
        this.deliveryFacade = (DeliveryFacade) deliveryFacade;
    }

    @Override
    public void handle(Contract contract) {

        Delivery delivery;
        switch (contract.getState()) {
            case CONTRACT_SIGNED:
                delivery = createDelivery(contract);
                stateManager.peerTriggerOption(delivery, ObjectStateTransitionAction.CREATE_DELIVERY);
                stateManager.peerTriggerOption(delivery, ObjectStateTransitionAction.ACTIVATE_DELIVERY);
                break;

            case CONTRACT_TERMINATED:
                Optional<Delivery> deliveryOptional = deliveryFacade.getByProjectId(contract.getProjectId());
                if (deliveryOptional.isPresent()) {
                    delivery = deliveryOptional.get();
                    logger.info("[handle] Closing delivery # deliveryId=" + delivery.getId());
                    stateManager.peerTriggerOption(deliveryOptional.get(), ObjectStateTransitionAction.CLOSE_DELIVERY);
                } else {
                    logger.warning("[handle] No delivery found for project # projectId=" + contract.getProjectId());
                }
                break;
        }
    }

    private Delivery createDelivery(Contract contract) {
        Delivery delivery = new Delivery();
        delivery.setId(IdUtils.recreateDeliveryId(contract.getProjectId()));
        delivery.setState(ObjectState.DELIVERY_NOT_CREATED);
        delivery.setDeliveryDate(contract.getDeliveryDate());
        delivery.setStartDate(contract.getStartDate());
        delivery.setBudget(contract.getBudget());
        delivery.setContractType(contract.getContractType());
        delivery.setPaymentType(contract.getPaymentType());
        delivery.setProjectId(contract.getProjectId());
        return delivery;
    }
}