package de.deutschebahn.ilv.smartcontract.client;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 08.09.2017.
 */
public class ChaincodeManagementService {

    private static final Logger logger = Logger.getLogger(ChaincodeManagementService.class.getName());
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 6, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100));
    static DeploymentPackageService deploymentPackageService = new DeploymentPackageService();
    static Channel channel;
    static FabricClient fabricClient;
    static String chaincodePath = "D:\\DEV\\ILV\\chaincode\\smartcontract";
    static boolean usePresetedCCVersion = true;
    static String presetedCCVersion = "3";

    public static void main(String[] args) throws IOException, InterruptedException {


        String factoryName = "config.properties";
        Properties factoryProperties = getProperties(factoryName);
        fabricClient = FabricClientFactory.createFabricClient(factoryProperties);
        channel = fabricClient.createOrInitChannel(
                factoryProperties.getProperty("channelName"),
                factoryProperties.getProperty("channelConfigLocation")
        );

        Map<Future<String>, String> futures = new HashMap<>();
        futures.put(deployCC("offer"), "offer");
        futures.put(deployCC("demand"), "demand");
        futures.put(deployCC("contract"), "contract");
        futures.put(deployCC("project"), "project");
        futures.put(deployCC("delivery"), "delivery");
        futures.put(deployCC("userService"), "userservices");

        while (true) {
            if (threadPoolExecutor.getCompletedTaskCount() == futures.size()) {
                threadPoolExecutor.shutdown();
                break;
            }
            Thread.sleep(500);
        }
        futures.forEach((k, v) -> {
            logger.info("[main] " + v + " done: " + k.isDone());
        });

        logger.info("[main] Finish!");

    }

    static Future<String> deployCC(String name) throws IOException, InterruptedException {
        String folder = name;
        //Unifiy names
        if (name.equalsIgnoreCase("userService")) {
            folder = "userservices";
        }

        deploymentPackageService.copyAll(Paths.get(chaincodePath + "\\" + folder + "\\src\\main\\java\\de\\deutschebahn\\ilv\\smartcontract"));
        installChaincode(fabricClient, name, channel);
//        Thread.sleep(500);
        return threadPoolExecutor.submit(() -> {
            try {
                return name + ":" + instantiateChaincode(fabricClient, name, channel);
            } catch (IOException e) {
                logger.severe("[deployCC] " + e.getMessage());
                e.printStackTrace();
                return name + ":KO" + e.getMessage();
            }
        });

    }

    static boolean instantiateChaincode(FabricClient fabricClient, String objectName, Channel channel) throws IOException {

        Properties chaincodeProperties = getProperties(objectName + ".properties");


        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeProperties.getProperty("name"))
                .setVersion(chaincodeProperties.getProperty("version"))
                .build();

        ProposalsResult proposalsResult = fabricClient.instantiateOrUpgradeChaincode(
                chaincodeID,
                channel,
                chaincodeProperties.getProperty("endorsementPolicyConfigFile")
        );

        logger.info("[instantiateChaincode] instantiateOrUpgradeChaincode " + objectName + " ProposalsResult = " + proposalsResult.getMessage());

        return proposalsResult.successfull();
    }

    static ProposalsResult installChaincode(FabricClient fabricClient, String objectName, Channel channel) throws IOException {
        Properties chaincodeProperties = getProperties(objectName + ".properties");
        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeProperties.getProperty("name"))
                .setVersion(chaincodeProperties.getProperty("version"))
                .build();

        ProposalsResult proposalsResult = fabricClient.installChaincode(
                chaincodeID,
                chaincodeProperties.getProperty("sourceLocation"),
                new HashSet<Peer>() {{
                    add(fabricClient.getAssignedPeer());
                }}
        );

        logger.info("[instantiateChaincode] installChaincode " + objectName + "  ProposalsResult = " + proposalsResult.getMessage());
        return proposalsResult;
    }

    static Properties getProperties(String fileName) throws IOException {

        String environment = System.getenv("ENVIRONMENT");
        if (environment == null) {
            environment = "default";
        }

        InputStream inputStream = ChaincodeManagementService.class.getClassLoader()
                .getResourceAsStream(environment + "/" + fileName);

        Properties properties = new Properties();
        properties.load(inputStream);

        if (usePresetedCCVersion) {
            properties.setProperty("version", presetedCCVersion);
        }

        return properties;
    }
}
