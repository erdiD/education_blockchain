package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public abstract class ObjectUpdatedHandlerTemplate<T extends BusinessObject> {
    private final DataConverter<T> dataConverter;
    private final ChaincodeStub chaincodeStub;

    public ObjectUpdatedHandlerTemplate(ChaincodeStub chaincodeStub, DataConverter<T> dataConverter) {
        this.chaincodeStub = chaincodeStub;
        this.dataConverter = dataConverter;
    }

    private void handle(ObjectNotification objectNotification) {
        T object = dataConverter.deserialize(objectNotification.toJsonObject(), DataConverter.DeserializeView.objectBetweenChaincodes);
        handle(object);
    }

    protected ChaincodeStub getChaincodeStub() {
        return chaincodeStub;
    }

    public abstract void handle(T object);
}
