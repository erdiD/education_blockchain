package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;

/**
 * Created by AlbertLacambraBasil on 23.07.2017.
 */
public class Printer {

    public static final String DEFAULT_STRING = "--";

    public static String toString(ChaincodeID chaincodeID) {

        if (chaincodeID == null) {
            return DEFAULT_STRING;
        }

        String pretty = new StringBuilder("name=").append(chaincodeID.getName())
                .append(", version=").append(chaincodeID.getVersion())
                .append(", path=").append(chaincodeID.getPath())
                .toString();

        return pretty;
    }

    public static String toString(Channel channel) {
        if (channel == null) {
            return DEFAULT_STRING;
        }

        String pretty = new StringBuilder("name=").append(channel.getName()).toString();
        return pretty;
    }

    public static String toString(BlockEvent.TransactionEvent transactionEvent) {

        if (transactionEvent == null) {
            return DEFAULT_STRING;
        }

        return new StringBuilder("TxId=").append(transactionEvent.getTransactionID())
                .append(", valid=").append(transactionEvent.isValid())
                .toString();
    }

    public static String toString(ProposalsResult result) {

        if (result == null) {
            return DEFAULT_STRING;
        }

        return new StringBuilder("success=").append(result.successfull())
                .append(", message=").append(result.getMessage())
                .toString();
    }

    public static String toString(Peer peer) {

        if (peer == null) {
            return DEFAULT_STRING;
        }

        return new StringBuilder("peerName=").append(peer.getName())
                .append(", peerUrl=").append(peer.getUrl())
                .toString();
    }
}
