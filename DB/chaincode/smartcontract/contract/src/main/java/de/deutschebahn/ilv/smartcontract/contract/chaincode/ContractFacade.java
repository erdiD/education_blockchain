package de.deutschebahn.ilv.smartcontract.contract.chaincode;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.smartcontract.business.BusinessObjectFacade;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class ContractFacade extends BusinessObjectFacade<Contract> {
    public ContractFacade(DataConverter<Contract> contractDataConverter, ChaincodeStub chaincodeStub) {
        super(contractDataConverter, chaincodeStub);
    }
}