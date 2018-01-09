package de.deutschebahn.ilv.smartcontract;

import org.hyperledger.fabric.sdk.BlockEvent;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
class TxInfo {
    String domain;
    String key;
    BlockEvent.TransactionEvent transactionEvent;

    public TxInfo(String domain, String key, BlockEvent.TransactionEvent transactionEvent) {
        this.domain = domain;
        this.key = key;
        this.transactionEvent = transactionEvent;
    }

    public String getDomain() {
        return domain;
    }

    public String getKey() {
        return key;
    }

    public BlockEvent.TransactionEvent getTransactionEvent() {
        return transactionEvent;
    }
}
