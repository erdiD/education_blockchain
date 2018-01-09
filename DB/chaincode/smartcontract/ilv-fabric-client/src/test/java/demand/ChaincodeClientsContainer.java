package demand;

import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.client.contract.ContractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.delivery.DeliveryChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.demand.DemandChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.offer.OfferChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.user.UserServiceChaincodeClient;
import de.deutschebahn.ilv.smartcontract.commons.ContractDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DemandDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.OfferDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;
import org.hyperledger.fabric.sdk.ChaincodeID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlbertLacambraBasil on 13.10.2017.
 */
public class ChaincodeClientsContainer {

    Map<String, UserClientContainer> containerMap = new HashMap<>();
    SmartContractClient smartContractClient;
    ChaincodeID demandChaincodeID;
    ChaincodeID offerChaincodeID;
    ChaincodeID contractChaincodeID;
    ChaincodeID userServicesChaincodeID;

    public ChaincodeClientsContainer(SmartContractClient smartContractClient,
                                     ChaincodeID demandChaincodeID,
                                     ChaincodeID offerChaincodeID,
                                     ChaincodeID contractChaincodeID,
                                     ChaincodeID userServicesChaincodeID) {

        this.smartContractClient = smartContractClient;
        this.demandChaincodeID = demandChaincodeID;
        this.offerChaincodeID = offerChaincodeID;
        this.contractChaincodeID = contractChaincodeID;
        this.userServicesChaincodeID = userServicesChaincodeID;
    }

    public ChaincodeClientsContainer registerUser(String userId) {
        containerMap.computeIfAbsent(userId, key -> new UserClientContainer(
                userId, smartContractClient, demandChaincodeID, offerChaincodeID, userServicesChaincodeID, contractChaincodeID
        ));

        return this;
    }

    public UserClientContainer getContainer(String userId) {
        return containerMap.get(userId);
    }

    public static class UserClientContainer {
        private String userId;
        private UserServiceChaincodeClient userServiceChaincodeClient;
        private OfferChaincodeClient offerChaincodeClient;
        private DemandChaincodeClient demandChaincodeClient;
        private ContractChaincodeClient contractChaincodeClient;

        DeliveryChaincodeClient deliveryChaincodeClient;

        public UserClientContainer(String userId, SmartContractClient smartContractClient,
                                   ChaincodeID demandChaincodeID,
                                   ChaincodeID offerChaincodeID,
                                   ChaincodeID userServicesChaincodeID,
                                   ChaincodeID contractChaincodeID
        ) {
            this.userId = userId;
            userServiceChaincodeClient = new UserServiceChaincodeClient(userId, smartContractClient, new UserDataConverter(), userServicesChaincodeID);
            demandChaincodeClient = new DemandChaincodeClient(userId, smartContractClient, new DemandDataConverter(), demandChaincodeID);
            offerChaincodeClient = new OfferChaincodeClient(userId, smartContractClient, new OfferDataConverter(), offerChaincodeID);
            contractChaincodeClient = new ContractChaincodeClient(userId, smartContractClient, new ContractDataConverter(), contractChaincodeID);
        }

        public String getUserId() {
            return userId;
        }

        public UserServiceChaincodeClient getUserServiceChaincodeClient() {
            return userServiceChaincodeClient;
        }

        public OfferChaincodeClient getOfferChaincodeClient() {
            return offerChaincodeClient;
        }

        public DemandChaincodeClient getDemandChaincodeClient() {
            return demandChaincodeClient;
        }

        public ContractChaincodeClient getContractChaincodeClient() {
            return contractChaincodeClient;
        }

        public DeliveryChaincodeClient getDeliveryChaincodeClient() {
            return deliveryChaincodeClient;
        }

    }
}
