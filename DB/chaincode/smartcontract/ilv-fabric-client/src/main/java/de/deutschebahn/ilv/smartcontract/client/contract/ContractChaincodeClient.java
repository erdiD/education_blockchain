package de.deutschebahn.ilv.smartcontract.client.contract;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.sdk.ChaincodeID;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ContractChaincodeClient extends BusinessObjectClient<Contract> {
    public ContractChaincodeClient(String userId, SmartContractClient client, DataConverter<Contract> dataConverter, ChaincodeID chaincodeID) {
        super(userId, client, dataConverter, chaincodeID);
    }

    @Override
    protected void rebuildUnicodeCompositeKeySymbol(Contract object) {
        super.rebuildUnicodeCompositeKeySymbol(object);
        object.setOfferId(replaceToUnicode(object.getOfferId()));
    }

    @Override
    protected void removeUnicodeCompositeKeySymbol(Contract object) {
        super.removeUnicodeCompositeKeySymbol(object);
        object.setOfferId(replaceToExternalSymbol(object.getOfferId()));
    }
}
