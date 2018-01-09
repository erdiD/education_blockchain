package de.deutschebahn.ilv.smartcontract.business.remote;

import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class ChaincodeInvocationMessage extends de.deutschebahn.ilv.smartcontract.commons.ChaincodeInvocationMessage {
    public ChaincodeInvocationMessage(ChaincodeStub chaincodeStub) {
        super(chaincodeStub.getFunction(), chaincodeStub.getParameters());
    }

    public ChaincodeInvocationMessage(String function, String principalId, List<String> params, Type type) {
        super(function, principalId, params, type);
    }

    @Override
    public long getMessageId() {
        return super.getMessageId();
    }
}