package de.deutschebahn.ilv.smartcontract.safelog.client;

//import de.deutschebahn.ilv.smartcontract.managment.*;

/**
 * Created by AlbertLacambraBasil on 23.07.2017.
 */
public class SafeLogChaincodeClient {

//    private static final String CHAINCODE_NAME = "SafeLog";
//    SmartContractClient smartContractClient = null;
//    private final FabricClient fabricClient;
//    private ChaincodeID chaincodeID;
//    private final Channel channel;
//    private final Duration WAIT_FOR_BLOCK_MAX_DURATION = Duration.of(10, ChronoUnit.SECONDS);
//
//    private Logger logger = Logger.getLogger(SafeLogChaincodeClient.class.getName());
//
//    public SafeLogChaincodeClient(FabricClient fabricClient,
//                                  Set<Peer> peers,
//                                  String channelName,
//                                  String channelFilePath,
//                                  String chaincodeSourceLocation,
//                                  String chaincodePath,
//                                  String chaincodeVersion,
//                                  String endorsementPolicy) throws FabricException {
//
//        Objects.requireNonNull(fabricClient);
//        Objects.requireNonNull(channelName);
//        Objects.requireNonNull(chaincodeSourceLocation);
//        Objects.requireNonNull(chaincodePath);
//        Objects.requireNonNull(chaincodeVersion);
//
//        this.fabricClient = fabricClient;
//        channel = fabricClient.createOrInitChannel(channelName, channelFilePath);
//        logger.info("[SafeLogChaincodeClient] channel loaded. " + Printer.toString(channel));
//
//        chaincodeID = ChaincodeID
//                .newBuilder()
//                .setName(CHAINCODE_NAME)
//                .setPath(chaincodePath)
//                .setVersion(chaincodeVersion)
//                .build();
//
//        ProposalsResult proposalsResult = fabricClient.installChaincode(chaincodeID, chaincodeSourceLocation, peers);
//
//        if (!proposalsResult.successfull()) {
//            logger.warning("[SafeLogChaincodeClient] Installation failed:" + proposalsResult.getMessage());
//            FabricException.chaincodeInstallationFailed(chaincodeID, peers, proposalsResult);
//        }
//
//        logger.info("[SafeLogChaincodeClient] client created for chaincodeId " + Printer.toString(chaincodeID));
//         smartContractClient = new SmartContractClient()
//        proposalsResult = fabricClient.instantiateOrUpgradeChaincode(chaincodeID, channel, endorsementPolicy);
////        if (proposalsResult.canBeSendToOrderer()) {
////            fabricClient.sendTransactionToOrderer(channel, proposalsResult).thenApply(transactionEvent -> {
////                        logger.info("[SafeLogChaincodeClient] Event received:"
////                                + transactionEvent.getTransactionID()
////                                + " is valid: " + transactionEvent.isValid());
////                        return transactionEvent;
////                    }
////            );
////        } else if (!proposalsResult.proposalSucceeded()) {
////            logger.warning("[SafeLogChaincodeClient] Instantiation or upgrade from CC failed.Error=" + proposalsResult.getMessage());
////            FabricException.chaincodeInstantiationFailed(chaincodeID, peers, proposalsResult);
////        }
//    }
//
//    public CompletableFuture<TransactionResult> createNewLogEntry(String domain, String loggingKey, String loggingValue) {
//        ProposalsResult proposalsResult = fabricClient.invokeChaincode(
//                channel, chaincodeID, "save",
//                new String[]{domain, loggingKey, loggingValue}
//        );
//        logger.info("[createNewLogEntry] invoking chaincode. TransactionId=" + proposalsResult.getTransactionId());
//        CompletableFuture<TransactionResult> futureTransactionResult;
//
//        if (proposalsResult.successfull()) {
//            futureTransactionResult = fabricClient.sendTransactionToOrdererAndConfirm(channel, proposalsResult);
//        } else {
//            logger.info("[createNewLogEntry] Proposal failed for TxId= " + proposalsResult.getTransactionId()
//                    + ". Error=" + proposalsResult.getMessage());
//            futureTransactionResult = new CompletableFuture<>();
//
//            TransactionResult failedTransactionResult = TransactionResult.createFailedResult(
//                    proposalsResult.getTransactionId(),
//                    "Proposal failed. Error=" + proposalsResult.getMessage()
//            );
//
//            futureTransactionResult.complete(failedTransactionResult);
//        }
//
//        return futureTransactionResult;
//    }
//
//    private TransactionResult getTransactionResultSynchronously(
//            String domain,
//            String loggingKey,
//            String loggingValue) throws FabricException {
//
//        TransactionResult transactionResult;
//        try {
//            transactionResult = createNewLogEntry(domain, loggingKey, loggingValue)
//                    .get(WAIT_FOR_BLOCK_MAX_DURATION.toMillis(), TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            throw new FabricException(e);
//        }
//
//        return transactionResult;
//    }
//
//    public String getLogEntriesOfId(String domain, String loggingKey) {
//        String[] args = new String[]{domain, loggingKey};
//        JsonArray r = fabricClient.queryChainCode(channel, chaincodeID, "queryByLogKey", args, transformerToJsonArray);
//        logger.info("[getLogEntriesOfId] result received:" + r.toString());
//        return r.toString();
//    }
//
//    public String getLogEntriesOfDomain(String domain) {
//        String[] args = new String[]{domain};
//        JsonObject r = fabricClient.queryChainCode(channel, chaincodeID, "queryByDomain", args, transformerToJsonObject);
//        logger.info("[getLogEntriesOfId] result received:" + r.toString());
//
//        return r.toString();
//    }
//
//    private Function<ProposalsResult, String> transformResultToJsonString = (proposalsResult) -> {
//        String jsonBody = "";
//        if (proposalsResult.proposalSucceeded()) {
//            jsonBody = proposalsResult.getSuccessful()
//                    .stream()
//                    .peek(pr -> logger.info("[transformerToJsonObject] Received body=" + pr.getProposalResponse().getResponse().getPayload().toStringUtf8()))
//                    .findAny()
//                    .flatMap(pr -> Optional.ofNullable(pr.getProposalResponse().getResponse().getPayload().toStringUtf8()))
//                    .filter(str -> !str.isEmpty())
//                    .orElse("");
//
//        }
//
//        return jsonBody;
//    };
//
//    private Function<ProposalsResult, JsonObject> transformerToJsonObject = transformResultToJsonString
//            .andThen(jsonString -> {
//                if (jsonString.isEmpty()) {
//                    return Json.createObjectBuilder().build();
//                } else {
//                    return Json.createReader(new StringReader(jsonString)).readObject();
//                }
//            });
//
//    private Function<ProposalsResult, JsonArray> transformerToJsonArray = transformResultToJsonString
//            .andThen(jsonString -> {
//                if (jsonString.isEmpty()) {
//                    return Json.createArrayBuilder().build();
//                } else {
//                    return Json.createReader(new StringReader(jsonString)).readArray();
//                }
//            });
}
