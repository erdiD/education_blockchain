package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Demand;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by AlbertLacambraBasil on 26.10.2017.
 */
public class AsyncObjectFetcher {

    @Inject
    DemandFacade demandFacade;

    @Asynchronous
    public Optional<Demand> getDemand(String demandId) {
        return demandFacade.getById(demandId);
    }

}
