package demand;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.CommunicationResult;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClientFactory;
import de.deutschebahn.ilv.smartcontract.client.demand.DemandChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.offer.OfferChaincodeClient;
import de.deutschebahn.ilv.smartcontract.commons.ActionPerformedEvent;
import de.deutschebahn.ilv.smartcontract.commons.EntityBuilder;
import de.deutschebahn.ilv.smartcontract.commons.MessageStatus;
import de.deutschebahn.ilv.smartcontract.commons.model.ActionInvocation;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static de.deutschebahn.ilv.domain.ObjectStateTransitionAction.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ChaincodeClientIT {

    private static final Logger logger = Logger.getLogger(ChaincodeClientIT.class.getName());
    private ChaincodeID demandChaincodeID;
    private ChaincodeID offerChaincodeID;
    private ChaincodeID projectChaincodeID;
    private ChaincodeID userServiceChaincodeID;
    private ChaincodeID contractChaincodeID;
    private Properties properties;
    private ChaincodeClientsContainer.UserClientContainer demandCreator;
    private ChaincodeClientsContainer.UserClientContainer offerCreator;
    private ChaincodeClientsContainer.UserClientContainer userCommercialApproval;
    private ChaincodeClientsContainer.UserClientContainer userTechnicalApproval;
    private ChaincodeClientsContainer.UserClientContainer custometSigner;
    private ChaincodeClientsContainer.UserClientContainer supplierSigner;
    private ChaincodeClientsContainer.UserClientContainer offerApproval;
    private Channel channel;
    String dbsystelId = "dbs_id";
    private String dbenergyId = "dbe_id";
    private ChaincodeClientsContainer clientsContainer;

    @BeforeClass
    public static void beforeClass() {
        String key = "org.hyperledger.fabric.sdk.configuration";
        String value = "src/main/resources/dev/config.properties";
        System.setProperty(key, value);
    }


    @Before
    public void init() throws IOException {

        String propFileName = "config.properties";
        InputStream inputStream = ChaincodeClientIT.class.getClassLoader().getResourceAsStream(propFileName);
        properties = new Properties();
        properties.load(inputStream);
        String chaincodeVersion = "1";

        demandChaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)

                .setName("DemandChaincode")
                .build();

        offerChaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)
                .setName("OfferChaincode")
                .build();

        userServiceChaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)
                .setName("UserServiceChaincode")
                .build();

        projectChaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)
                .setName("ProjectChaincode")
                .build();

        contractChaincodeID = ChaincodeID.newBuilder()
                .setVersion(chaincodeVersion)
                .setName("ContractChaincode")
                .build();

        loadChaincodeClients();
        registerChaincodeListener(ActionPerformedEvent.NAME);
    }

    @Test
    public void registerUser() {
        User u = createUser("alacambra", "Albert", "Lacambra", dbenergyId, MarketRoleName.DEMAND_CREATOR, MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL);
        clientsContainer.getContainer(u.getId()).getUserServiceChaincodeClient().registerUser(u);
    }

    private User createUser(String username, String firstName, String lastName, String orgId, MarketRoleName... roles) {
        User user = new User();
        user.setUserName(username);
        user.setFirsName(firstName);
        user.setLastName(lastName);
        user.setId(username + "_id");
        if (roles.length > 0) {
            user.setMarketRole(Arrays.asList(roles));
        }
        user.setOrganizationId(orgId);
        user.setLastModified(new Date());
        user.setDateCreated(new Date());
        clientsContainer.registerUser(user.getId());
        return user;
    }

    class ChaincodeEventCapture {
        final String handle;
        final BlockEvent blockEvent;
        final ChaincodeEvent chaincodeEvent;

        ChaincodeEventCapture(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
            this.handle = handle;
            this.blockEvent = blockEvent;
            this.chaincodeEvent = chaincodeEvent;
        }
    }

    private List<ChaincodeEventCapture> chaincodeEvents = new ArrayList<>();

    private String registerChaincodeListener(String eventName) {
        String chaincodeEventListenerHandle = null;
        try {
            chaincodeEventListenerHandle = channel.registerChaincodeEventListener(
                    Pattern.compile(".*"),
                    Pattern.compile(Pattern.quote(eventName)),
                    (handle, blockEvent, chaincodeEvent) -> {
                        chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

                        System.out.println(String.format(
                                "RECEIVED Chaincode event with handle: %s, chaincode Id: %s, chaincode event name: %s, transaction id: %s, event payload: \"%s\", from eventhub: %s",
                                handle,
                                chaincodeEvent.getChaincodeId(),
                                chaincodeEvent.getEventName(),
                                chaincodeEvent.getTxId(),
                                new String(chaincodeEvent.getPayload()),
                                blockEvent.getEventHub().toString())
                        );
                    });

        } catch (InvalidArgumentException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return chaincodeEventListenerHandle;
    }

    private void loadChaincodeClients() {
        SmartContractClient smartContractClient = SmartContractClientFactory.createFabricClient(properties);
        smartContractClient.tryInitChannel();
        channel = smartContractClient.getChannel();
        clientsContainer = new ChaincodeClientsContainer(
                smartContractClient,
                demandChaincodeID,
                offerChaincodeID,
                contractChaincodeID,
                userServiceChaincodeID);

        clientsContainer
                .registerUser("christian_id")
                .registerUser("sonja_id")
                .registerUser("demandcreator_id")
                .registerUser("userCommAppr_id")
                .registerUser("userTechAppr_id")
                .registerUser("offerAppr_id")
                .registerUser("signerdbe_id")
                .registerUser("signerdbs_id")
                .registerUser("pmdbs_id");

        demandCreator = clientsContainer.getContainer("christian_id");
        offerCreator = clientsContainer.getContainer("sonja_id");
        offerApproval = clientsContainer.getContainer("offerAppr_id");
        userCommercialApproval = clientsContainer.getContainer("userCommAppr_id");
        userTechnicalApproval = clientsContainer.getContainer("userTechAppr_id");
        custometSigner = clientsContainer.getContainer("signerdbe_id");
        supplierSigner = clientsContainer.getContainer("signerdbs_id");
        demandCreator.getUserServiceChaincodeClient().initUsers();
    }

    @Test
    public void test() throws Exception {
        Demand demand = createAndUpdateDemand();
        fireAction(demandCreator.getDemandChaincodeClient(), demand.getId(), SUBMIT_DEMAND);
        Offer offer = createAndUpdateOffer(demand.getProjectId());
        fireAction(offerCreator.getOfferChaincodeClient(), offer.getId(), REVIEW_OFFER);
        fireAction(offerApproval.getOfferChaincodeClient(), offer.getId(), APPROVE_OFFER_INTERNAL);
        fireAction(demandCreator.getOfferChaincodeClient(), offer.getId(), ACCEPT_OFFER);
        fireAction(userCommercialApproval.getOfferChaincodeClient(), offer.getId(), APPROVE_OFFER);
        fireAction(userTechnicalApproval.getOfferChaincodeClient(), offer.getId(), APPROVE_OFFER);
        fireAction( demandCreator.getDemandChaincodeClient(), demand.getId(), RESIGN_DEMAND);
        String contractId = offer.getProjectId() + "\u0000C\u0000";
        String demandId = offer.getProjectId() + "\u0000D\u0000";
        CommunicationResult<Demand> result = supplierSigner.getDemandChaincodeClient().getById(demandId);
        assertThat(result.getMessageStatus(), is(MessageStatus.OK));
        fireAction(supplierSigner.getContractChaincodeClient(), contractId, SIGN_CONTRACT);
        fireAction(custometSigner.getContractChaincodeClient(), contractId, SIGN_CONTRACT);
    }

    private Demand createAndUpdateDemand() throws ExecutionException {
        DemandChaincodeClient demandChaincodeClient = demandCreator.getDemandChaincodeClient();
        EntityBuilder<Demand, DemandBuilder> demandBuilder = EntityBuilder.createDemand();
        demandBuilder.getBuilder().withId(null).withProjectId(null);
        Demand demand = demandBuilder.getEntity();
        assertThat(demand.getId(), is(nullValue()));

        demand = demandChaincodeClient.create(demand).getResult();
        assertThat(demand.getId(), not(nullValue()));

        String id = demand.getId();
        demand = demandChaincodeClient.getById(demand.getId()).getResult();
        assertThat(demand.getId(), is(id));

        BigDecimal newBudget = demand.getBudget().add(BigDecimal.TEN);
        demand.setBudget(newBudget);
        demand = demandChaincodeClient.update(demand).getResult();
        assertThat(demand.toString(), demand.getBudget(), is(newBudget));

        return demand;
    }

    private void fireAction(BusinessObjectClient client, String objectId, ObjectStateTransitionAction action) {
        client.fireAction(new ActionInvocation(action, objectId));
    }

    private Offer createAndUpdateOffer(String projectId) throws ExecutionException {
        EntityBuilder<Offer, OfferBuilder> offerBuilder = EntityBuilder.createOffer();
        Offer offer = offerBuilder.getBuilder().withId(null).withProjectId(projectId).withOrganizationId("dbs_id").build();

        OfferChaincodeClient offerChaincodeClient = offerCreator.getOfferChaincodeClient();

        Offer createdOffer = offerChaincodeClient.create(offer).getResult();
        Offer receivedOffer = offerChaincodeClient.getById(createdOffer.getId()).getResult();
        assertThat(receivedOffer.getId(), not(isEmptyOrNullString()));

        String newDescription = UUID.randomUUID().toString();

        receivedOffer.setDescription(newDescription);
        Offer updatedOffer = offerChaincodeClient.update(receivedOffer).getResult();
        assertThat(receivedOffer == updatedOffer, is(false));
        assertThat(updatedOffer, is(receivedOffer));
        assertThat(updatedOffer.getDescription(), is(newDescription));

        return updatedOffer;
    }
}
