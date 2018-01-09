package de.deutschebahn.ilv.smartcontract;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
public class ChannelCreationAndDeploymentIT {

//    private static final String CHAINCODE_NAME = "SafeLog";
//    private static final String DOMAIN = "ILV-2";
//    private FabricClient fabricClient;
//    private static List<TxInfo> toQuery = Collections.synchronizedList(new ArrayList<>());
//    private ChaincodeID chaincodeID;
//    private Channel channel;
//    private Logger logger = Logger.getLogger(SafeLogChaincodeClient.class.getName());
//    private static final int TOTAL_ENTRIES = 2;
//    private static final int NUM_THREADS = 1;
//    private static CountDownLatch latch = new CountDownLatch(TOTAL_ENTRIES);
//
//    @Test
//    public void testFlow() throws IOException, InterruptedException {
//
//        String propFileName = "config.properties";
//        InputStream inputStream = SafeLogChaincodeClient.class.getClassLoader().getResourceAsStream(propFileName);
//        Properties properties = new Properties();
//        properties.load(inputStream);
//
//        String chaincodeVersion = PropertyReader.getValueOrException("chaincodeVersion", properties::getProperty);
//        String chaincodePath = PropertyReader.getValueOrException("chaincodePath", properties::getProperty);
//        String chaincodeSourceLocation = PropertyReader.getValueOrException("chaincodeSourceLocation", properties::getProperty);
//        String endorsementPolicyConfigFile = PropertyReader.getValueOrException("endorsementPolicyConfigFile", properties::getProperty);
//        String channelConfigLocation = PropertyReader.getValueOrException("channelConfigLocation", properties::getProperty);
//
//        FabricClient fabricClient = FabricClientFactory.createFabricClient(properties);
//        SafeLogChaincodeClient safeLogChaincodeClient = new SafeLogChaincodeClient(
//                fabricClient,
//                new HashSet<>(FabricClientFactory.getEndorserPeers()),
//                "mychannel",
//                channelConfigLocation,
//                chaincodeSourceLocation,
//                chaincodePath,
//                chaincodeVersion,
//                endorsementPolicyConfigFile
//        );
//
//        ExecutorService executorService = new ThreadPoolExecutor(
//                NUM_THREADS, NUM_THREADS
//                , 5,
//                TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(500),
//                new ThreadPoolExecutor.DiscardOldestPolicy()
//        );
//
//        Long start = System.currentTimeMillis();
//
//        for (int i = 0; i < TOTAL_ENTRIES; i++) {
//            final int j = i;
//            Runnable r = () -> {
//                safeLogChaincodeClient
//                        .createNewLogEntry(DOMAIN, "ObjectD" + (j), "that is the log value")
//                        .thenAccept(transactionResult -> {
//                            logger.info(transactionResult.toString());
//                            if (!transactionResult.isSuccessful()) {
//                                logger.info("[testFlow] Unsuccessful tx=" + transactionResult);
//                                System.exit(-1);
//                            }
//                            latch.countDown();
//                        }).exceptionally(e -> {
//                            logger.info("Exception received:" + e);
//                            System.exit(-1);
//                            return null;
//                        }
//                );
//
//            };
//            executorService.execute(r);
//        }
//
//        latch.await(20, TimeUnit.SECONDS);
//        Long end = System.currentTimeMillis();
//        logger.info("Total creation time=" + (end - start));
//        logger.info("Total entries: " + toQuery.size());
//        logger.info("[testFlow] througput=" + ((double) TOTAL_ENTRIES / (end - start)));
//
//
//        for (TxInfo txInfo : new ArrayList<>(toQuery)) {
//            logger.info("TEST:" + txInfo.getDomain());
//            logger.info("TEST:" + txInfo.getKey());
//            logger.info("TEST:" + txInfo.getTransactionEvent().getTransactionActionInfo(0).getResponseMessage());
//            logger.info("RESULT: " + safeLogChaincodeClient.getLogEntriesOfId(txInfo.getDomain(), txInfo.getKey()));
//        }
//
//        logger.info("Received transaction: " + "total= " + toQuery.size() + toQuery.stream()
//                .map(txInfo -> txInfo.getTransactionEvent().getTransactionID())
//                .collect(Collectors.joining(", ")));
//
//        logger.info("DOMAIN:");
//        logger.info(safeLogChaincodeClient.getLogEntriesOfDomain(DOMAIN));
//
//        logger.info("total duration:" + (System.currentTimeMillis() - start));
//        executorService.shutdown();
//    }
}
