package de.deutschebahn.ilv.smartcontract.delivery.chaincode;

import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import de.deutschebahn.ilv.smartcontract.business.BusinessObjectFacade;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DeliveryEntryDataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 02.10.2017.
 */
public class DeliveryFacade extends BusinessObjectFacade<Delivery> {
    private static final Logger logger = Logger.getLogger(DeliveryFacade.class.getName());

    public DeliveryFacade(DataConverter<Delivery> dataConverter, ChaincodeStub chaincodeStub) {
        super(dataConverter, chaincodeStub);
    }

    public Optional<Delivery> getByProjectId(String projectId) {
        String deliveryId = IdUtils.recreateDeliveryId(projectId);
        return getById(deliveryId);
    }

    @Override
    public Optional<Delivery> getById(String id) {
        Optional<Delivery> deliveryOptional = super.getById(id);
        deliveryOptional.ifPresent(delivery -> {
            //TODO injection
            DeliveryEntryDataConverter deliveryEntryDataConverter = new DeliveryEntryDataConverter();
            String allEntriesId = IdUtils.recreateDeliveryEntryId(delivery.getProjectId());
            QueryResultsIterator<KeyValue> keyValues = getChaincodeStub().getStateByPartialCompositeKey(allEntriesId);
            ArrayList<DeliveryEntry> objects = new ArrayList<>();
            for (KeyValue keyValue : keyValues) {
                JsonObject jsonObject = stringToJsonObject(new String(keyValue.getValue()));
                DeliveryEntry object = deliveryEntryDataConverter.deserialize(jsonObject, DataConverter.DeserializeView.jsonInDatabaseToObjectInFabric);
                objects.add(object);
            }
            logger.info("[getById] DeliveryEntries load # DeliveryEntries=" + objects);
            delivery.setDeliveryEntries(objects);
        });
        return deliveryOptional;
    }

    public void addPsps(Delivery delivery, Collection<String> values) {
        delivery.getPsps().addAll(values);
    }
}
