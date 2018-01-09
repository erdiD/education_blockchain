package de.deutschebahn.ilv.smartcontract.client.offer;

import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeInvocationMessage;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeResponseMessage;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import de.deutschebahn.ilv.smartcontract.commons.model.ObjectList;
import org.hyperledger.fabric.sdk.ChaincodeID;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class OfferChaincodeClient extends BusinessObjectClient<Offer> {
    private static final Logger logger = Logger.getLogger(OfferChaincodeClient.class.getName());

    public OfferChaincodeClient(String userId, SmartContractClient client, DataConverter<Offer> dataConverter, ChaincodeID chaincodeID) {
        super(userId, client, dataConverter, chaincodeID);
    }

    public List<Offer> getByProjectId(String projectId) {
        projectId = replaceToUnicode(projectId);
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(
                GenericActions.getByProjectId.name(),
                getLoggedUserId(),
                Collections.singletonList(projectId),
                ChaincodeInvocationMessage.Type.USER
        );
        ChaincodeResponseMessage responseMessage = invoke(invocationMessage);
        if (responseMessage.getStatus().isSuccessful()) {
            List<Offer> offers = new ObjectList<>(responseMessage.getPayload(), getDataConverter()).getObjects();
            offers.forEach(this::removeUnicodeCompositeKeySymbol);
            return offers;
        } else {
            logger.info("[getByProjectId] Problem fetching offers # responseMessage=" + responseMessage);
            return Collections.emptyList();
        }
    }
}
