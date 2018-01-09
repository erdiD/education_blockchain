package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.contract.ContractChaincodeClient;

import javax.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class ContractFacade extends BusinessObjectFacade<Contract> {

    private static final Logger logger = Logger.getLogger(ContractFacade.class.getName());

    @Inject
    ContractChaincodeClient contractChaincodeClient;

    public Optional<Contract> getContractByProject(Demand demand) {
        return getContractByProject(demand.getProjectId());
    }

    public Optional<Contract> getContractByProject(String projectId) {
        return checkCommunicationResultAndReturn(contractChaincodeClient.getById(projectId));
    }

    @Override
    protected ContractChaincodeClient getChaincodeClient() {
        return contractChaincodeClient;
    }

    @Override
    protected BusinessObjectClient<Contract> getBusinessObjectClient() {
        return contractChaincodeClient;
    }
}
