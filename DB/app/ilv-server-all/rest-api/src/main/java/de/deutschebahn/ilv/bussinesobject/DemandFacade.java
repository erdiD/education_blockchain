package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.demand.DemandChaincodeClient;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class DemandFacade extends BusinessObjectFacade<Demand> {

    private static final Logger logger = Logger.getLogger(DemandFacade.class.getName());

    @Inject
    DemandChaincodeClient demandChaincodeClient;

    @Override
    protected AbstractChaincodeClient<Demand> getChaincodeClient() {
        return demandChaincodeClient;
    }

    @Override
    protected BusinessObjectClient<Demand> getBusinessObjectClient() {
        return demandChaincodeClient;
    }
}
