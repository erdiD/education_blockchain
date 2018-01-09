package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.BlockInfo;

import java.time.Duration;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
public class TransactionConfirmationService {

    private static final Duration DELAY = Duration.ofSeconds(5);
    private static final Duration PERIOD = Duration.ofSeconds(5);
    private static final int MAX_BLOCKS = 10;

    private static volatile TransactionConfirmationService instance;

    private Queue<BlockInfo> receivedBlocks;

    private TransactionConfirmationService() {
        this.receivedBlocks = new ConcurrentLinkedQueue<>();
    }

    public CompletableFuture<String> confirmTransaction(String transactionId) {
        Objects.requireNonNull(transactionId);
        return new CompletableFuture<>();
    }

    private void controlSize() {
        synchronized (receivedBlocks) {
            if (receivedBlocks.size() < MAX_BLOCKS) {
                return;
            }

            int blocksToRemove = receivedBlocks.size() - MAX_BLOCKS;
            for (int i = 0; i < blocksToRemove; i++) {
                receivedBlocks.poll();
            }
        }
    }

    private void addNewBlock(BlockInfo blockInfo) {
        receivedBlocks.add(blockInfo);
        controlSize();
    }

    public static TransactionConfirmationService get() {
        if (instance == null) {
            synchronized (TransactionConfirmationService.class) {
                if (instance == null) {
                    instance = new TransactionConfirmationService();
                }
            }
        }

        return instance;
    }

    private static class WaitingTransaction {
        private String transactionId;
        private CompletableFuture<String> transactionConfirmed;
    }
}
