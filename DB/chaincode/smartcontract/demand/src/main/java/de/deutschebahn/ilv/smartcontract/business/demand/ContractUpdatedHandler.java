package de.deutschebahn.ilv.smartcontract.business.demand;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedHandlerTemplate;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 10.10.2017.
 */
public class ContractUpdatedHandler extends ObjectUpdatedHandlerTemplate<Contract> {

    private static final Logger logger = Logger.getLogger(ContractUpdatedHandler.class.getName());
    private final StateManager<Demand> stateManager;
    private final ObjectFacade<Demand> objectFacade;

    public ContractUpdatedHandler(ChaincodeStub chaincodeStub, DataConverter<Contract> dataConverter, StateManager<Demand> stateManager, ObjectFacade<Demand> objectFacade) {
        super(chaincodeStub, dataConverter);
        this.stateManager = stateManager;
        this.objectFacade = objectFacade;
    }

    @Override
    //TODO: add return message. Should help to validate notification chains
    public void handle(Contract object) {
        if (object.getState() == ObjectState.CONTRACT_SIGNED) {

            this.logger.info("[handleContractEvents] setting demand to locked state =" + object);
            String demandId = IdUtils.recreateDemandId(object.getProjectId());
            Demand demand = objectFacade.getById(demandId).orElseThrow(RuntimeException::new);
            stateManager.peerTriggerOption(demand, ObjectStateTransitionAction.LOCK_DEMAND);
        }
    }
}
