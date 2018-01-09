package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by AlbertLacambraBasil on 22.07.2017.
 */
public class FabricClient {

    private static final String[] EMPTY_ARGS = new String[0];

    /**
     * Peer used for simulations and queries
     */
    private final Peer assignedPeer;
    private final Logger logger = Logger.getLogger(FabricClient.class.getName());
    private final HFClient client;
    private final Orderer assignedOrderer;
    private final TransactionRequest.Type chaincodeType = TransactionRequest.Type.JAVA;

    //TODO: are all the channels going to use the same eventHub?
    private final EventHub eventHub;
    private long proposalWaitTime;

    FabricClient(HFClient client,
                 Peer assignedPeer,
                 Orderer assignedOrderer,
                 long proposalWaitTime,
                 EventHub eventHub) {
        this.eventHub = eventHub;

        Objects.requireNonNull(client);
        Objects.requireNonNull(assignedPeer);
        Objects.requireNonNull(assignedOrderer);

        this.client = client;
        this.assignedPeer = assignedPeer;
        this.assignedOrderer = assignedOrderer;
        this.proposalWaitTime = proposalWaitTime;

        logger.info("[BasicFabricClient] Assigned peer is: " + Printer.toString(assignedPeer));

    }

    public Channel createOrInitChannel(String channelName, String channelFilePath) {
        Objects.requireNonNull(channelName);
        Channel channel;

        if (channelAlreadyExist(channelName)) {
            logger.info("[createOrInitChannel] Channel already created. Reconstructing it....");
            channel = initChannel(channelName);
        } else {
            Objects.requireNonNull(channelFilePath);
            logger.info("[createOrInitChannel] Channel NOT created. Creating it....");
            channel = createChannel(channelName, new File(channelFilePath + "/" + channelName + ".tx"));
        }

        try {
            channel.addOrderer(assignedOrderer);
            channel.addEventHub(eventHub);
            channel.initialize();
        } catch (InvalidArgumentException | TransactionException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    private boolean channelAlreadyExist(String channelName) {
        Set<String> channels = null;
        try {
            channels = client.queryChannels(assignedPeer);
        } catch (InvalidArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (ProposalException e) {
            logger.warning("[channelAlreadyExist] ProposalExceptions happened. Error=" + e.getMessage());
            throw new RuntimeException(e);
        }
        return channels.contains(channelName);
    }

    public Channel initChannel(String channelName) {

        Channel channel;

        try {
            channel = client.newChannel(channelName);
            channel.addPeer(assignedPeer);
        } catch (InvalidArgumentException e) {
            throw new IllegalArgumentException(e);
        }

        return channel;
    }

    private Channel createChannel(String channelName, File channelFile) {

        Channel channel;

        try {
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);
            channel = client.newChannel(channelName, assignedOrderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));
            channel.joinPeer(assignedPeer);
        } catch (IOException | ProposalException | InvalidArgumentException | TransactionException e) {
            throw new RuntimeException(e);
        }

        return channel;
    }


    public ProposalsResult installChaincode(ChaincodeID chaincodeID, String chaincodeSourceLocation, Set<Peer> endorsementPeers) {

        Objects.requireNonNull(chaincodeID);
        Objects.requireNonNull(chaincodeSourceLocation);
        Objects.requireNonNull(endorsementPeers);

        if (endorsementPeers.isEmpty()) {
            throw new IllegalArgumentException("endorsementPeers cannot be empty");
        }

        if (isChaincodeInstalled(assignedPeer, chaincodeID)) {
            return ProposalsResult.createSuccess("Chaincode already installed");
        }

        Collection<ProposalResponse> responses;

        logger.info("[installChaincode] Installing chaincode. Preparing Request Structure to Install chaincode");
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);

        try {
            installProposalRequest.setChaincodeSourceLocation(new File(chaincodeSourceLocation));
        } catch (InvalidArgumentException e) {
            logger.warning("[installChaincode] Problems loading ccSourceLocation. Error=" + e.getMessage());
        }

        installProposalRequest.setChaincodeLanguage(chaincodeType);
        installProposalRequest.setChaincodeVersion(chaincodeID.getVersion());

        int numInstallProposal = endorsementPeers.size();

        logger.info("[installChaincode] Sending Install Request for the Install Chaincode to Peers: " + String.valueOf(numInstallProposal));

        try {
            //TODO: should endorsementPeers include assignedPeer?
            responses = client.sendInstallProposal(installProposalRequest, endorsementPeers);
        } catch (ProposalException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }

        ProposalsResult proposalsResult = ProposalsResult.of(responses);
        return proposalsResult;
    }


    public ProposalsResult instantiateOrUpgradeChaincode(ChaincodeID chaincodeID, Channel channel, String endorsementPolicyConfigFile) {
        Collection<ProposalResponse> responses = Collections.emptyList();
        boolean shouldUpgrade;
        boolean shouldInstantiate;
        Peer peer = assignedPeer;

        /*
        Fetch all chaincodes of peer that matches with the given name. It may have several versions
         */
        Set<Query.ChaincodeInfo> chaincodeInfos = getInstantiatedChaincodeOfChannel(peer, channel)
                .stream()
                .filter(chaincodeInfo -> chaincodeInfo.getName().equals(chaincodeID.getName()))
                .collect(Collectors.toSet());


        shouldInstantiate = chaincodeInfos.isEmpty();
        shouldUpgrade = !shouldInstantiate && chaincodeInfos
                .stream()
                .noneMatch(chaincodeInfo -> chaincodeInfo.getVersion().equals(chaincodeID.getVersion()));

        try {
            if (shouldInstantiate) {
                logger.info("[instantiateOrUpgradeChaincode] Chaincode will be instantiated. ChaincodeID=" + Printer.toString(chaincodeID));
                InstantiateProposalRequest instantiateProposalRequest = instantiateChainCodeProposalRequest(chaincodeID, endorsementPolicyConfigFile);
                responses = channel.sendInstantiationProposal(instantiateProposalRequest);
            } else if (shouldUpgrade) {
                logger.info("[instantiateOrUpgradeChaincode] Chaincode will be updated. ChaincodeID=" + Printer.toString(chaincodeID));
                UpgradeProposalRequest upgradeProposalRequest = upgradeChainCodeProposalRequest(chaincodeID, endorsementPolicyConfigFile);
                responses = channel.sendUpgradeProposal(upgradeProposalRequest);
            } else {
                logger.info("[instantiateOrUpgradeChaincode] " +
                        "no need to instantiateChaincode or upgrade. " +
                        "Chaincode exists on the correct version");

                return ProposalsResult.createSuccess("no need to instantiateChaincode or upgrade. Chaincode exists on the correct version");
            }
        } catch (ProposalException | InvalidArgumentException e) {
            logger.warning("[instantiateOrUpgradeChaincode] An error happens. Error=" + e.getMessage());
            return ProposalsResult.createFailed(e.getMessage());
        }

        ProposalsResult proposalsResult = ProposalsResult.of(responses);

        if (proposalsResult.canBeSendToOrderer()) {

            sendTransactionToOrderer(channel, proposalsResult).thenApply(transactionEvent -> {
                logger.info("[SafeLogChaincodeClient] Event received:"
                        + transactionEvent.getTransactionID()
                        + " is valid: " + transactionEvent.isValid());
                return transactionEvent;
            });
        }

        return proposalsResult;
    }

    public Set<Query.ChaincodeInfo> getChaincodeInfoOfPeer(Peer peer) {
        Set<Query.ChaincodeInfo> chaincodeInfos = Collections.emptySet();
        try {
            chaincodeInfos = new HashSet<>(client.queryInstalledChaincodes(peer));
        } catch (InvalidArgumentException | ProposalException e) {
            logger.warning("[getChaincodeInfoOfPeer] Exception when fetching chaincodes of peer="
                    + peer.getName()
                    + ". Error=" + e.getMessage()
            );
        }
        return chaincodeInfos;
    }

    public Set<Query.ChaincodeInfo> getInstantiatedChaincodeOfChannel(Peer peer, Channel channel) {
        Set<Query.ChaincodeInfo> chaincodeInfos = Collections.emptySet();
        try {
            chaincodeInfos = new HashSet<>(channel.queryInstantiatedChaincodes(peer));
        } catch (InvalidArgumentException | ProposalException e) {
            logger.warning("[getInstantiatedChaincodeOfChannel] Exception when fetching chaincodes of peer="
                    + peer.getName()
                    + ". Error=" + e.getMessage()
            );
        }
        return chaincodeInfos;
    }

    public boolean isChaincodeInstalled(Peer peer, ChaincodeID chaincodeID) {

        String cc_name = chaincodeID.getName();
        String cc_path = chaincodeID.getPath();
        String cc_version = chaincodeID.getVersion();

        String printableCCId = String.format(
                "chaincode: %s, at version: %s, on peer: %s"
                , cc_name, cc_version, peer.getName());

        logger.info("[isChaincodeInstalled] Checking cc " + printableCCId);

        Set<Query.ChaincodeInfo> chaincodeInfos;
        chaincodeInfos = getChaincodeInfoOfPeer(peer);

        boolean found = false;

        for (Query.ChaincodeInfo chaincodeInfo : chaincodeInfos) {
            found = cc_name.equals(chaincodeInfo.getName()) && cc_path.equals(chaincodeInfo.getPath()) && cc_version.equals(chaincodeInfo.getVersion());
            if (found) {
                break;
            }
        }

        logger.info("[isChaincodeInstalled] Chaincode found=" + found + ", " + printableCCId);
        return found;
    }

    public boolean isChaincodeOnCurrentVersion(ChaincodeID chaincodeID, Query.ChaincodeInfo chaincodeInfo) {
        return chaincodeInfo.getVersion().equals(chaincodeID.getVersion());
    }

    private CompletableFuture<BlockEvent.TransactionEvent> sendTransactionToOrderer(Channel channel, ProposalsResult proposalsResult) {

        if (!proposalsResult.successfull()) {
            logger.warning("[sendTransactionToOrderer] Trying to send an unsuccessful proposal. Ignoring it...");
            CompletableFuture<BlockEvent.TransactionEvent> cf = new CompletableFuture<>();
            cf.completeExceptionally(new RuntimeException("proposal has failed:" + proposalsResult.getMessage()));
            return cf;
        }

        return channel.sendTransaction(proposalsResult.getSuccessful(), Arrays.asList(assignedOrderer));
    }

    private CompletableFuture<TransactionResult> sendTransactionToOrdererAndConfirm(Channel channel, ProposalsResult proposalsResult) {

        return sendTransactionToOrderer(channel, proposalsResult).thenApply(transactionEvent -> {
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


    private <T extends TransactionRequest> T prepareGenericProposalRequest(
            ChaincodeID chaincodeID,
            Supplier<T> proposalRequestSupplier,
            String endorsementPolicyConfigFile) {

        T proposalRequest = prepareGenericProposalRequest(chaincodeID, proposalRequestSupplier);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = getChaincodeEndorsementPolicy(endorsementPolicyConfigFile);
        if (chaincodeEndorsementPolicy != null) {
            proposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        return proposalRequest;
    }

    private <T extends TransactionRequest> T prepareGenericProposalRequest(ChaincodeID chaincodeID,
                                                                           Supplier<T> proposalRequestSupplier) {

        T proposalRequest = proposalRequestSupplier.get();

        proposalRequest.setChaincodeID(chaincodeID);
        proposalRequest.setProposalWaitTime(proposalWaitTime);
        proposalRequest.setFcn("init");
        proposalRequest.setChaincodeLanguage(chaincodeType);
        proposalRequest.setArgs(new ArrayList<>(0));
        proposalRequest.setUserContext(client.getUserContext());

        return proposalRequest;
    }

    private ChaincodeEndorsementPolicy getChaincodeEndorsementPolicy(String endorsementPolicyConfigFile) {
        if (endorsementPolicyConfigFile == null) {
            return null;
        }
        return getChaincodeEndorsementPolicy(new File(endorsementPolicyConfigFile));
    }

    private ChaincodeEndorsementPolicy getChaincodeEndorsementPolicy(File endorsementPolicyConfigFile) {
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        try {
            chaincodeEndorsementPolicy.fromYamlFile(endorsementPolicyConfigFile);
            logger.info("[getChaincodeEndorsementPolicy] Policies correctly loaded. FileName=" + endorsementPolicyConfigFile);
        } catch (IOException | ChaincodeEndorsementPolicyParseException e) {
            throw new FabricException(e);
        }

        return chaincodeEndorsementPolicy;
    }

    private UpgradeProposalRequest upgradeChainCodeProposalRequest(ChaincodeID chaincodeID, String endorsementPolicyConfigFile) {

        UpgradeProposalRequest upgradeProposalRequest =
                prepareGenericProposalRequest(chaincodeID, client::newUpgradeProposalRequest, endorsementPolicyConfigFile);

        return upgradeProposalRequest;
    }

    private InstantiateProposalRequest instantiateChainCodeProposalRequest(ChaincodeID chaincodeID, String endorsementPolicyConfigFile) {

        InstantiateProposalRequest instantiateProposalRequest =
                prepareGenericProposalRequest(chaincodeID, client::newInstantiationProposalRequest, endorsementPolicyConfigFile);

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));

        try {
            instantiateProposalRequest.setTransientMap(tm);
        } catch (InvalidArgumentException e) {
            logger.warning("[upgradeChainCodeProposalRequest] Problems setting TransientMap. Error=" + e.getMessage());
        }

        return instantiateProposalRequest;
    }

    public Peer getAssignedPeer() {
        return assignedPeer;
    }

    public HFClient getClient() {
        return client;
    }
}
