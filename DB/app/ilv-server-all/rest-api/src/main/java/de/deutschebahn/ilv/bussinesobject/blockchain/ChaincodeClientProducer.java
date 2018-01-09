package de.deutschebahn.ilv.bussinesobject.blockchain;

import de.deutschebahn.ilv.app.Environment;
import de.deutschebahn.ilv.app.user.LoggedUser;
import de.deutschebahn.ilv.smartcontract.client.Printer;
import de.deutschebahn.ilv.smartcontract.client.PropertyReader;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.client.contract.ContractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.delivery.DeliveryChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.demand.DemandChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.offer.OfferChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.user.UserServiceChaincodeClient;
import de.deutschebahn.ilv.smartcontract.commons.*;
import org.hyperledger.fabric.sdk.ChaincodeID;

import javax.ejb.Stateful;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 04.09.2017.
 */
@Stateful
public class ChaincodeClientProducer implements Serializable {
    private static final Logger logger = Logger.getLogger(ChaincodeClientProducer.class.getName());

    @Inject
    SmartContractClient smartContractClient;

    @Inject
    @Environment
    String environment;

    @Inject
    LoggedUser loggedUser;

    @Produces
    public DemandChaincodeClient getDemandChaincodeClient() throws IOException {
        ChaincodeID chaincodeID = getChaincodeID("demand");
        return new DemandChaincodeClient(loggedUser.getUser().getId(), smartContractClient, new DemandDataConverter(), chaincodeID);
    }

    @Produces
    public ContractChaincodeClient getContractChaincodeClient() throws IOException {
        ChaincodeID chaincodeID = getChaincodeID("contract");
        return new ContractChaincodeClient(loggedUser.getUser().getId(), smartContractClient, new ContractDataConverter(), chaincodeID);
    }

    @Produces
    public OfferChaincodeClient getOfferChaincodeClient() throws IOException {
        ChaincodeID chaincodeID = getChaincodeID("offer");
        return new OfferChaincodeClient(loggedUser.getUser().getId(), smartContractClient, new OfferDataConverter(), chaincodeID);
    }

    @Produces
    public DeliveryChaincodeClient getDeliveryChaincodeClient() throws IOException {
        ChaincodeID chaincodeID = getChaincodeID("delivery");
        return new DeliveryChaincodeClient(loggedUser.getUser().getId(), smartContractClient, new DeliveryDataConverter(), chaincodeID);
    }

    @Produces
    public UserServiceChaincodeClient getUserServiceChaincodeClient() throws IOException {
        ChaincodeID chaincodeID = getChaincodeID("userService");
        //TODO: service should be able to login....
        return new UserServiceChaincodeClient("", smartContractClient, new UserDataConverter(), chaincodeID);
    }

    private ChaincodeID getChaincodeID(String name) throws IOException {
        String propFileName = environment + "/" + name + ".properties";
        logger.info("[getChaincodeID] Loading property file resource.# file=" + propFileName);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) {
            throw new RuntimeException("property file resource not found. # fileResource=" + propFileName);
        }

        Properties properties = new Properties();
        properties.load(inputStream);
        String chaincodeVersion = PropertyReader.getValueOrException("version", properties::getProperty);
        String chaincodeName = PropertyReader.getValueOrException("name", properties::getProperty);
        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)
                .setName(chaincodeName)
                .build();

        logger.info("[getChaincodeID] Creating chaincodeId=" + Printer.toString(chaincodeID));
        return chaincodeID;
    }
}
