package de.deutschebahn.ilv.smartcontract.client;

import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
public class TransactionResult {

    private Long blockId;
    private String transactionId;
    private FabricException fabricException;
    private boolean isSuccessful;
    private String message;

    private TransactionResult(Long blockId, String transactionId) {
        this.blockId = blockId;
        this.transactionId = transactionId;
        isSuccessful = true;
    }

    private TransactionResult(String transactionId, FabricException fabricException) {
        this.transactionId = transactionId;
        this.fabricException = fabricException;
        this.message = fabricException.getMessage();
        isSuccessful = false;
    }

    private TransactionResult(String transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
        isSuccessful = false;
    }

    public boolean isSuccessful() {
        return blockId != null;
    }

    public Long getBlockId() {
        return blockId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public FabricException getFabricException() {
        return fabricException;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "de.deutschebahn.ilv.smartcontract.managment.de.deutschebahn.ilv.smartcontract.client.TransactionResult{" +
                "blockId=" + blockId +
                ", transactionId='" + transactionId + '\'' +
                ", fabricException=" + fabricException +
                ", isSuccessful=" + isSuccessful +
                '}';
    }

    public static TransactionResult createSuccessfulResult(Long blockId, String transactionId) {
        Objects.requireNonNull(blockId, "blockId must be given");
        Objects.requireNonNull(transactionId, "transactionId must be given");
        return new TransactionResult(blockId, transactionId);
    }

    public static TransactionResult createFailedResult(String transactionId, String message) {
        Objects.requireNonNull(transactionId, "transactionId must be given");
        Objects.requireNonNull(message, "message must be given");
        return new TransactionResult(transactionId, message);
    }

    public static TransactionResult createFailedResult(String transactionId, FabricException fabricException) {
        Objects.requireNonNull(transactionId, "transactionId must be given");
        Objects.requireNonNull(fabricException, "message must be given");
        return new TransactionResult(transactionId, fabricException);
    }
}
