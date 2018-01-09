package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.offer.OfferChaincodeClient;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by KSchwartz on 16.08.2017.
 */
public class OfferFacade extends BusinessObjectFacade<Offer> {

    private static final Logger logger = Logger.getLogger(OfferFacade.class.getName());

    @Inject
    OfferChaincodeClient offerChaincodeClient;

    public List<Offer> getOffersOfProject(String projectId) {
        return getBusinessObjectClient().getByProjectId(projectId);
    }

    @Override
    protected AbstractChaincodeClient<Offer> getChaincodeClient() {
        return offerChaincodeClient;
    }

    @Override
    protected OfferChaincodeClient getBusinessObjectClient() {
        return offerChaincodeClient;
    }
}
