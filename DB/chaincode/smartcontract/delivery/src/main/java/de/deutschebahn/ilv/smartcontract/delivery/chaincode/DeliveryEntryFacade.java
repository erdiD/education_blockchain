package de.deutschebahn.ilv.smartcontract.delivery.chaincode;

import de.deutschebahn.ilv.domain.DeliveryEntry;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.commons.DeliveryEntryDataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Optional;

/**
 * Created by AlbertLacambraBasil on 02.10.2017.
 */
public class DeliveryEntryFacade extends ObjectFacade<DeliveryEntry> {

    public DeliveryEntryFacade(DeliveryEntryDataConverter dataConverter, ChaincodeStub chaincodeStub) {
        super(dataConverter, chaincodeStub);
    }

    public Optional<DeliveryEntry> getByProjectId(String projectId) {
        String deliveryId = IdUtils.recreateDeliveryId(projectId);
        return getById(deliveryId);
    }
}
