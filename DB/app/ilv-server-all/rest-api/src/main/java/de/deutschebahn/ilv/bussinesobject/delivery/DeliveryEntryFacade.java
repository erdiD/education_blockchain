package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.bussinesobject.ObjectFacade;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.delivery.DeliveryChaincodeClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 14.08.2017.
 */
@Stateless
public class DeliveryEntryFacade extends ObjectFacade<DeliveryEntry> {

    @Inject
    Logger logger;

    @Inject
    DeliveryChaincodeClient deliveryChaincodeClient;

    @Override
    protected AbstractChaincodeClient<DeliveryEntry> getChaincodeClient() {
        //TODO Implement it
//        return deliveryChaincodeClient;
        throw new UnsupportedOperationException();
    }
}