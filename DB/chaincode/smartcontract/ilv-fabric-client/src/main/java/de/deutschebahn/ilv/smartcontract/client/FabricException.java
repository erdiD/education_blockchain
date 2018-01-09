package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Peer;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
public class FabricException extends RuntimeException {
    private ProposalsResult result;

    public static FabricException chaincodeInstallationFailed(
            ChaincodeID chaincodeID, Collection<Peer> peers, ProposalsResult result) {

        return new FabricException("Installation of chaincode failed.  Error= "
                + result.getMessage()
                + ". ChaincodeID= " + Printer.toString(chaincodeID)
                + ", Peers= " + getPrintedPeers(peers),
                result);
    }

    public static FabricException chaincodeInstantiationFailed(
            ChaincodeID chaincodeID, Collection<Peer> peers, ProposalsResult result) {

        return new FabricException("Instantiation of chaincode failed. Error= "
                + result.getMessage()
                + ". ChaincodeID= " + Printer.toString(chaincodeID)
                + ", Peers= " + getPrintedPeers(peers),
                result);
    }

    private static String getPrintedPeers(Collection<Peer> peers) {
        return peers.stream().map(Printer::toString).collect(Collectors.joining(" # "));
    }


    public FabricException() {
    }

    public FabricException(String text, ProposalsResult result) {
        this(text);
        this.result = result;
    }

    public FabricException(String message) {
        super(message);
    }

    public FabricException(String message, Throwable cause) {
        super(message, cause);
    }

    public FabricException(Throwable cause) {
        super(cause);
    }

    public FabricException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public Optional<ProposalsResult> getResult() {
        return Optional.ofNullable(result);
    }
}
