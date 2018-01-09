package de.deutschebahn.ilv.smartcontract.client.demand;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.sdk.ChaincodeID;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class DemandChaincodeClient extends BusinessObjectClient<Demand> {
    public DemandChaincodeClient(String userId, SmartContractClient client, DataConverter<Demand> dataConverter, ChaincodeID chaincodeID) {
        super(userId, client, dataConverter, chaincodeID);
    }
}