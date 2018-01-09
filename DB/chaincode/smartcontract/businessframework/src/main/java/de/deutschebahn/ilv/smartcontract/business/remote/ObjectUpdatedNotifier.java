package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.smartcontract.business.LocalObjectUpdatedHandler;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.JsonObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
public class ObjectUpdatedNotifier<T extends BusinessObject> {

    private final ChaincodeTargetFactory targetFactory;
    private final DataConverter<T> dataConverter;
    private final Map<String, ChaincodeTarget> chaincodeTargets;
    private LocalObjectUpdatedHandler<T> handler;


    public ObjectUpdatedNotifier(ChaincodeTargetFactory targetFactory, DataConverter<T> dataConverter) {
        this.targetFactory = targetFactory;
        this.dataConverter = dataConverter;
        chaincodeTargets = new ConcurrentHashMap<>();
    }

    public void notifyUpdate(ChaincodeStub stub, T object) {
        JsonObject jsonObject = dataConverter.serialize(object, DataConverter.SerializeView.objectBetweenChaincodes);
        ObjectNotification objectNotification = new ObjectNotification(object.getClass().getSimpleName(), jsonObject);
        chaincodeTargets.values().forEach(chaincodeMethodInvocation ->
                chaincodeMethodInvocation
                        .withChaincodeStub(stub)
                        .function(GenericActions.objectUpdated.name())
                        .params(objectNotification.toJsonObject().toString())
                        .asPeer()
                        .build()
                        .execute()
        );

        if (handler != null) {
            handler.handle(object);
        }
    }

    public void addNotifier(String chaincodeName) {
        chaincodeTargets.computeIfAbsent(chaincodeName, targetFactory::getOrCreate);
    }

    public void setLocalNotifier(LocalObjectUpdatedHandler<T> handler) {
        this.handler = handler;
    }
}
