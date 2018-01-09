package de.deutschebahn.ilv.smartcontract.business.demand;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.smartcontract.business.BusinessObjectFacade;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class DemandFacade extends BusinessObjectFacade<Demand> {
    public DemandFacade(DataConverter<Demand> demandDataConverter, ChaincodeStub chaincodeStub) {
        super(demandDataConverter, chaincodeStub);
    }

    @Override
    protected boolean isCorrectType(JsonObject jsonObject) {
        return jsonObject.containsKey("targetAccount");
    }
}