package de.deutschebahn.ilv.smartcontract.offer;

import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.business.BusinessObjectFacade;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.JsonObject;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class OfferFacade extends BusinessObjectFacade<Offer> {

    private static final Logger logger = Logger.getLogger(OfferFacade.class.getName());

    public OfferFacade(DataConverter<Offer> offerDataConverter, ChaincodeStub chaincodeStub) {
        super(offerDataConverter, chaincodeStub);
    }

    @Override
    protected boolean isCorrectType(JsonObject jsonObject) {

        String id = jsonObject.getString("id", "false");
        //just to assure its not a attachment of the offer
        //TODO: do it ID based
        return jsonObject.containsKey("price");
    }
}