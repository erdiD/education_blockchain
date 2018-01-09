package de.deutschebahn.ilv.smartcontract.client.delivery;

import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.CommunicationResult;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.DeliveryEntrySet;
import org.hyperledger.fabric.sdk.ChaincodeID;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class DeliveryChaincodeClient extends BusinessObjectClient<Delivery> {

    private static final Logger logger = Logger.getLogger(DeliveryChaincodeClient.class.getName());

    public DeliveryChaincodeClient(String userId, SmartContractClient client, DataConverter<Delivery> dataConverter, ChaincodeID chaincodeID) {
        super(userId, client, dataConverter, chaincodeID);
    }

    public Optional<Delivery> getDeliveryByDemand(Demand demand) {
        return getDeliveryByDemandId(demand.getId());
    }

    public CommunicationResult<DeliveryEntrySet> saveProcessedDelivery(String projectId, DeliveryEntrySet deliveryEntrySet) {
        projectId = replaceToUnicode(projectId);
        deliveryEntrySet.getDeliveryEntries().stream().forEach(de -> {
            de.setDeliveryId(replaceToUnicode(de.getDeliveryId()));
        });
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(
                GenericActions.saveNewDeliveryEntries.name(),
                getLoggedUserId(),
                Arrays.asList(projectId, deliveryEntrySet.toJson().toString()),
                ChaincodeInvocationMessage.Type.USER
        );

        ChaincodeResponseMessage message = invoke(invocationMessage);
        //TODO: put it in correct place
        DeliveryEntrySet recDeliveryEntrySet = new DeliveryEntrySet(message.getPayload(), new AttachmentDataConverter(), new DeliveryEntryDataConverter(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp);
        recDeliveryEntrySet.getDeliveryEntries().stream().forEach(de -> {
            de.setDeliveryId(replaceToExternalSymbol(de.getDeliveryId()));
        });
        return wrapResult(message, recDeliveryEntrySet);
    }

    @Deprecated
    public Optional<Delivery> getDeliveryByDemandId(String demandId) {
//
//        ChaincodeResponseMessage messageResponse = queryChaincode("getDeliveryByDemandId", demandId);
//        if (messageResponse.getStatus().isSuccessful()) {
//            Delivery delivery = getDataConverter().deserialize(messageResponse.getPayload(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp);
//            return Optional.of(delivery);
//        } else {
        return Optional.empty();
//        }
    }
}