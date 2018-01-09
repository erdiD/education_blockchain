package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by AlbertLacambraBasil on 22.07.2017.
 */
public class SmartContractClient implements Serializable {

    private static final String[] EMPTY_ARGS = new String[0];

    private static final Function<ProposalsResult, ProposalsResult> identity = i -> i;
    /**
     * Peer used for simulations and queries
     */
    private final Peer assignedPeer;
    private static final Logger logger = Logger.getLogger(SmartContractClient.class.getName());
    private final HFClient client;

    /**
     * TODO: decide if orderers should be bound to client or not. Depends of user session or from CC invocation
     */
    private final Orderer assignedOrderer;
    private final TransactionRequest.Type chaincodeType = TransactionRequest.Type.JAVA;

    //TODO: are all the channels going to use the same eventHub?
    private final EventHub eventHub;
    private final String channelName;
    private long proposalWaitTime;
    private Channel channel;

    public SmartContractClient(HFClient client,
                               Peer assignedPeer,
                               Orderer assignedOrderer,
                               EventHub eventHub,
                               String channelName,
                               long proposalWaitTime
                               ) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(assignedPeer);
        Objects.requireNonNull(assignedOrderer);
        Objects.requireNonNull(eventHub);
        Objects.requireNonNull(channelName);

        this.client = client;
        this.assignedPeer = assignedPeer;
        this.assignedOrderer = assignedOrderer;
        this.proposalWaitTime = proposalWaitTime;
        this.eventHub = eventHub;
        this.channelName = channelName;

        logger.info("[BasicFabricClient] Assigned peer is: " + Printer.toString(assignedPeer));
    }


    public void tryInitChannel() {

        if(channel != null){
            return;
        }

        if (channelAlreadyExist(channelName)) {
            logger.info("[createOrInitChannel] Channel already created. Reconstructing it....");
            channel = initChannel(channelName);
        } else {
            throw new FabricException("Channel does not exist. Not possible to recreate");
        }

        try {
            channel.addOrderer(assignedOrderer);
            channel.addEventHub(eventHub);
            channel.initialize();
        } catch (InvalidArgumentException | TransactionException e) {
            throw new FabricException(e);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    private boolean channelAlreadyExist(String channelName) {
        Set<String> channels;
        try {
            channels = client.queryChannels(assignedPeer);
        } catch (InvalidArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (ProposalException e) {
            logger.warning("[channelAlreadyExist] ProposalExceptions happened. Error=" + e.getMessage());
            throw new FabricException(e);
        }
        return channels.contains(channelName);
    }

    private Channel initChannel(String channelName) {

        Channel channel;

        try {
            channel = client.newChannel(channelName);
            channel.addPeer(assignedPeer);
        } catch (InvalidArgumentException e) {
            throw new IllegalArgumentException(e);
        }

        return channel;
    }

    public CompletableFuture<BlockEvent.TransactionEvent> sendTransactionToOrderer(ProposalsResult proposalsResult) {

        if (!proposalsResult.successfull()) {
            logger.warning("[sendTransactionToOrderer] Trying to send an unsuccessful proposal. Ignoring it...");
            CompletableFuture<BlockEvent.TransactionEvent> cf = new CompletableFuture<>();
            cf.completeExceptionally(new RuntimeException("proposal has failed:" + proposalsResult.getMessage()));
            return cf;
        }

        return channel.sendTransaction(proposalsResult.getSuccessful(), Arrays.asList(assignedOrderer));
    }

    public CompletableFuture<TransactionResult> sendTransactionToOrdererAndConfirm(ProposalsResult proposalsResult) {

        return sendTransactionToOrderer(proposalsResult).thenApply(transactionEvent -> {
            String transactionId = proposalsResult.getTransactionId();
            try {
                BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionId);
                if (blockInfo == null) {
                    return TransactionResult.createFailedResult(transactionId, "BlockInfo not found for transaction=" + transactionId);
                } else {
                    return TransactionResult.createSuccessfulResult(blockInfo.getBlockNumber(), transactionId);
                }
            } catch (ProposalException e) {
                return TransactionResult.createFailedResult(transactionId, new FabricException(e));
            } catch (InvalidArgumentException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    public ProposalsResult invokeChaincode(ChaincodeID chaincodeID, String functionName, String... args) {

        if (channel == null) {
            tryInitChannel();
        }

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn(functionName);
        transactionProposalRequest.setProposalWaitTime(proposalWaitTime);
        transactionProposalRequest.setArgs(args);

        Map<String, byte[]> transientProposalData = new HashMap<>();
        transientProposalData.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        transientProposalData.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        transientProposalData.put("result", ":)".getBytes(UTF_8));

        try {
            transactionProposalRequest.setTransientMap(transientProposalData);

            Collection<ProposalResponse> transactionPropResp =
                    channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

            ProposalsResult proposalsResult = ProposalsResult.of(transactionPropResp);

            if (!proposalsResult.successfull()) {
                return proposalsResult;
            }

            Collection<Set<ProposalResponse>> invokeTRProposalConsistencySets =
                    SDKUtils.getProposalConsistencySets(transactionPropResp);

            //TODO: what is that?
            if (invokeTRProposalConsistencySets.size() != 1) {
                throw new RuntimeException(format("Expected only one set of consistent proposal responses but got %d", invokeTRProposalConsistencySets.size()));
            }

            return proposalsResult;

        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException(ex);
        } catch (ProposalException e) {
            return ProposalsResult.createFailed(e.getMessage());
        }
    }

    public byte[] queryChainCode(ChaincodeID chaincodeID,
                                 String functionName,
                                 String... args) {

        Objects.requireNonNull(chaincodeID);
        Objects.requireNonNull(functionName);

        logger.info(String.format("[queryChainCode] Querying chaincode=%s, functionName=%s, args=%s ",
                Printer.toString(chaincodeID), functionName, Arrays.asList(args)));

        ProposalsResult proposalsResult = queryChainCode(chaincodeID, functionName, identity, args);

        if (!proposalsResult.successfull()) {
            logger.info("[queryChainCode] " + proposalsResult.getMessage());
        }

        return proposalsResult.getBody();
    }

    public <R> R queryChainCode(ChaincodeID chaincodeID,
                                String functionName,
                                Function<ProposalsResult, R> resultTransformation,
                                String... args) {

        Objects.requireNonNull(chaincodeID);
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(resultTransformation);

        if (channel == null) {
            tryInitChannel();
        }

        if (args == null) {
            args = new String[0];
        }

        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(functionName);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        //TODO: is that really needed?
        Map<String, byte[]> transientProposalData = new HashMap<>();
        transientProposalData.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        transientProposalData.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));

        Collection<ProposalResponse> queryProposals = null;

        try {
            queryByChaincodeRequest.setTransientMap(transientProposalData);
            /*
             * Ask only to one peer. Several peers are needed if we want to assure consistency. If a node
             * is recovering we can compare blockIds and takes the eldest one.
             */
            queryProposals = channel.queryByChaincode(queryByChaincodeRequest, Arrays.asList(assignedPeer));

        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException(ex);
        } catch (ProposalException e) {
            logger.warning("[queryChainCode] ProposalException received. Error=" + e.getMessage());
            return resultTransformation.apply(ProposalsResult.createFailed(e.getMessage()));
        }

        return resultTransformation.apply(ProposalsResult.of(queryProposals));
    }


    public Peer getAssignedPeer() {
        return assignedPeer;
    }

    public HFClient getClient() {
        return client;
    }
}
