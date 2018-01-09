package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.ProposalResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 22.07.2017.
 */
public class ProposalsResult {

    private final boolean resultIsSuccess;
    private Logger logger = Logger.getLogger(ProposalsResult.class.getName());
    private Collection<ProposalResponse> successful = new ArrayList<>();
    private Collection<ProposalResponse> failed = new ArrayList<>();
    private String message = "no message given";
    private String transactionId;

    private ProposalsResult(Collection<ProposalResponse> responses) {
        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                logger.info("[ProposalsResult] Successful instantiateChaincode proposal response Txid: "
                        + response.getTransactionID()
                        + " from peer "
                        + response.getPeer().getName());
            } else {
                failed.add(response);
            }

            if (response.getTransactionID() != null) {
                if (transactionId != null && !transactionId.equals(response.getTransactionID())) {
                    logger.warning("[ProposalsResult] received responses with different transactionsIds." +
                            " Can it happen? Tx1 = " + transactionId + ", Tx2=" + response.getTransactionID());

                } else if (transactionId == null) {
                    transactionId = response.getTransactionID();
                }
            }
        }
        logger.info("[ProposalsResult] Received instantiateChaincode proposal responses. Successful+verified: "
                + successful.size() + " . Failed: " + failed.size());

        if (failed.size() > 0) {
            message = failed.stream().map(ProposalResponse::getMessage).collect(Collectors.joining(" | "));
            resultIsSuccess = false;
        } else {
            resultIsSuccess = true;
        }
    }

    public byte[] getBody() {
        Optional<ProposalResponse> proposalsResult = successful.stream().findAny();
        if (proposalsResult.isPresent()) {
            return proposalsResult.get().getProposalResponse().getResponse().getPayload().toByteArray();
        }

        return new byte[0];
    }

    private ProposalsResult(boolean resultIsSuccess, String reason) {
        this.resultIsSuccess = resultIsSuccess;
        this.message = reason;
    }

    public static ProposalsResult of(Collection<ProposalResponse> responses) {
        return new ProposalsResult(responses);
    }

    public static ProposalsResult createFailed(String reason) {
        Objects.requireNonNull(reason);
        return new ProposalsResult(false, reason);
    }

    public static ProposalsResult createSuccess(String reason) {
        Objects.requireNonNull(reason);
        return new ProposalsResult(true, reason);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean successfull() {
        return resultIsSuccess;
    }

    public Collection<ProposalResponse> getSuccessful() {
        return new ArrayList<>(successful);
    }

    public Collection<ProposalResponse> getFailed() {
        return new ArrayList<>(failed);
    }

    public boolean canBeSendToOrderer() {
        //it can happens that the proposal has not being actually send but succeds because it wasnt needed to send it
        //That means that eg. code was already instantiated.
        return resultIsSuccess && !successful.isEmpty();
    }

    public String getMessage() {
        return message;
    }
}
