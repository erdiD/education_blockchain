package de.deutschebahn.ilv.bussinesobject.blockchain;

import de.deutschebahn.ilv.app.FabricProperties;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClientFactory;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by AlbertLacambraBasil on 04.09.2017.
 */
@Singleton
@Startup
public class SmartContractClientProducer implements Serializable {

    @Inject
    @FabricProperties
    Properties properties;

    SmartContractClient smartContractClient;

    static {
        String key = "org.hyperledger.fabric.sdk.proposal.wait.time";
        String value = "120000000";
        System.setProperty(key, value);
    }

    @Produces
    public SmartContractClient createSmartContractClient() throws IOException {

        if (smartContractClient == null) {
            smartContractClient = SmartContractClientFactory.createFabricClient(properties);
        }

        return smartContractClient;
    }
}
