package de.deutschebahn.ilv.smartcontract.client;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 22.07.2017.
 */
public class SmartContractClientFactory {

    private static final Logger logger = Logger.getLogger(SmartContractClientFactory.class.getName());

    public static Collection<Peer> getEndorserPeers() {
        return endorserPeers;
    }

    private static Collection<Peer> endorserPeers;

    public static SmartContractClient createFabricClient(Properties prop) {
        Function<String, String> getProperty = prop::getProperty;
        logger.info("[createFabricClient] Using props" + prop.toString());


        String HFCA_PEM = PropertyReader.getValueOrException("HFCA_PEM", getProperty);
        String CA_URL = PropertyReader.getValueOrException("CA_URL", getProperty);
        String ADMIN_PEM = PropertyReader.getValueOrException("ADMIN_PEM", getProperty);
        String ADMIN_PK = PropertyReader.getValueOrException("ADMIN_PK", getProperty.andThen(SmartContractClientFactory::loadPrivateKey));
        String ORDERER_URL = PropertyReader.getValueOrException("ORDERER_URL", getProperty);
        String PEER_URL = PropertyReader.getValueOrException("PEER_URL", getProperty);
        String EVENT_HUB_URL = PropertyReader.getValueOrException("EVENTHUB_URL", getProperty);
        long PROPOSAL_WAIT_TIME = PropertyReader.getValueOrException("PROPOSALWAITTIME", getProperty.andThen(Long::valueOf));
        int MAX_INBOUND_MSG_SIZE = PropertyReader.getValueOrException("MAXINBOUNDMSGSIZE", getProperty.andThen(Integer::valueOf));
        Security.addProvider(new BouncyCastleProvider());

        HFClient client = initHFCAndPeerAdmin(HFCA_PEM, CA_URL, ADMIN_PEM, ADMIN_PK);
        Collection<Orderer> orderers = initOrderers(client, ORDERER_URL);

        if (orderers.isEmpty()) {
            throw new IllegalArgumentException("No orderers found");
        }
        Orderer assignedOrderer = orderers.iterator().next();

        endorserPeers = initPeers(client, MAX_INBOUND_MSG_SIZE, PEER_URL);
        if (endorserPeers.isEmpty()) {
            throw new IllegalArgumentException("No orderers found");
        }
        Peer assignedPeer = new ArrayList<>(endorserPeers).get(0);

        //TODO: Why several eventHubs?
        Collection<EventHub> eventHubs = initEventHubs(client, MAX_INBOUND_MSG_SIZE, EVENT_HUB_URL);
        if (eventHubs.isEmpty()) {
            throw new IllegalArgumentException("No evenHubs found");
        }
        EventHub eventHub = eventHubs.iterator().next();
        return new SmartContractClient(client, assignedPeer, assignedOrderer, eventHub, "mychannel", PROPOSAL_WAIT_TIME);
    }

    public static Collection<EventHub> initEventHubs(HFClient client, int maxInboundMsgSize, String evenHubUrl) {

        Collection<EventHub> eventHubs = new HashSet<>();
        Properties eventHubProperties = new Properties();

        eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
        eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
        eventHubProperties.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        eventHubProperties.setProperty("hostnameOverride", "peer0.org1.example.com");
        eventHubProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", maxInboundMsgSize);

        try {
            eventHubs.add(client.newEventHub("peer0.org1.example.com", evenHubUrl, eventHubProperties));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }

        return eventHubs;
    }

    private static Collection<Peer> initPeers(HFClient client, int maxInboundMsgSize, String peerUrl) {
        Collection<Peer> peers = new HashSet<>();
        Properties peerProperties = new Properties();
        peerProperties.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        peerProperties.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", maxInboundMsgSize);
        peerProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
        peerProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});

        try {
            peers.add(client.newPeer("peer0.org1.example.com", peerUrl, peerProperties));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
        return peers;
    }

    private static Collection<Orderer> initOrderers(HFClient client, String ordererUrl) {
        Collection<Orderer> orderers = new HashSet<>();
        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        ordererProperties.setProperty("hostnameOverride", "inst1.orderer1.example.com");
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});

        try {
            orderers.add(client.newOrderer("inst1.orderer1.example.com", ordererUrl, ordererProperties));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }

        return orderers;
    }

    private static HFClient initHFCAndPeerAdmin(String hfcaPemFile, String caUrl, String adminPem, String adminPk) {
        HFClient client = null;


        String storeFileLocation = System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties";
        File sampleStoreFile = new File(storeFileLocation);

        if (sampleStoreFile.exists()) {
            sampleStoreFile.delete();
        }

        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        client = HFClient.createNewInstance();

        try {
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

            Properties caProperties = new Properties();
            caProperties.setProperty("allowAllHostNames", "true");//testing environment only NOT FOR PRODUCTION!
            caProperties.setProperty("pemFile", hfcaPemFile);
            HFCAClient ca = HFCAClient.createNewInstance("ca.org1.example.com", caUrl, caProperties);
            ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

            SampleUser sampleUser = new SampleUser("admin", "org1.example.com", sampleStore);
            sampleUser.setMPSID("Org1MSP");
            sampleUser.setEnrollment(ca.enroll("admin", "adminpw"));
            System.out.println("admin user enrolled on CA1 for Org1");
            sampleUser.saveState();

            String certificate = new String(IOUtils.toByteArray(new FileInputStream(adminPem)), "UTF-8");
            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(adminPk)));
            SampleUser peerOrgAdmin = new SampleUser("Admin", "org1.example.com", sampleStore);
            peerOrgAdmin.setMPSID("Org1MSP");
            peerOrgAdmin.setEnrollment(new SampleStore.SampleStoreEnrollement(privateKey, certificate));
            logger.info("[initHFCAndPeerAdmin] peer admin user enrolled on CA1 for Org1");
            peerOrgAdmin.saveState();

            client.setUserContext(peerOrgAdmin);
        } catch (InvalidArgumentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException
                | IOException
                | EnrollmentException
                | InvalidKeySpecException
                | CryptoException
                | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    private static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {

        Reader pemReader = new StringReader(new String(data));
        PrivateKeyInfo pemPair;

        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }

        PrivateKey privateKey = new JcaPEMKeyConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getPrivateKey(pemPair);

        return privateKey;
    }

    private static String loadPrivateKey(String keyPath) {
        File[] files = new File(keyPath).listFiles();
        return keyPath + files[0].getName();
    }

}
