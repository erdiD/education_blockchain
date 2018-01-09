package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.Organization;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeInvocationMessage;
import de.deutschebahn.ilv.smartcontract.business.remote.ExceptionMapper;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.user.UserFacade;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ObjectChaincode extends ChaincodeBase {

    private Logger logger = Logger.getLogger(ObjectChaincode.class.getName());
    private ExceptionMapper exceptionMapper;
    private UserDataConverter userDataConverter;

    public ObjectChaincode() {
        exceptionMapper = new ExceptionMapper();
        userDataConverter = new UserDataConverter();
    }

    @Override
    public Response init(ChaincodeStub chaincodeStub) {
        return newSuccessResponse("Initialized");
    }

    @Override
    //TODO: should support login method
    public Response invoke(ChaincodeStub chaincodeStub) {
        try {
            ChaincodeInvocationMessage receivedMessage = new ChaincodeInvocationMessage(chaincodeStub);
            UserServicesChaincodeAction actionName = getAction(receivedMessage.getFunction());
            List<String> params = receivedMessage.getParams();
            UserFacade userFacade = new UserFacade(userDataConverter, chaincodeStub);
            ChaincodeResponseMessage response;


            switch (actionName) {

                case regiserUser:
                    response = createUser(userFacade, params);
                    break;
                case getById:
                    //TODO: fix it. A method should be used when for login, so no principal exist yet.
                    // Can be fixed here or in the UserServiceCCClient

                    String userId = receivedMessage.getPrincipalId();
                    if (userId == null || userId.isEmpty()) {
                        userId = receivedMessage.getParams().get(0);
                    }
                    response = getById(userFacade, userId);
                    break;
                case getByUsername:
                    response = getByUsername(chaincodeStub, params);
                    break;
                case create:
                    response = create(userFacade, params);
                    break;
                case update:
                    response = update(userFacade, params);
                    break;
                case initUsers:
                    initUsers(userFacade);
                default:
                    response = new ChaincodeResponseMessage(MessageStatus.NO_METHOD_FOUND);
            }

            return newSuccessResponse(response.asBytes());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return exceptionMapper.handleExceptionAndGetResponse(e);
        }
    }

    private ChaincodeResponseMessage getById(UserFacade userFacade, String userId) {
        User user = userFacade.getById(userId).orElseThrow(() -> ClientException.notFoundError(userId, User.class));
        JsonObject jsonObject = userDataConverter.serialize(user, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private ChaincodeResponseMessage getByUsername(ChaincodeStub chaincodeStub, List<String> args) {
//        String username = args.get(0);
//        UserDataConverter userDataConverter = new UserDataConverter();
//        QueryResultsIterator<KeyValue> r = chaincodeStub.getQueryResult(String.format("{username:%s}", username));
//        return StreamSupport.stream(r.spliterator(), false)
//                .map(KeyValue::getValue)
//                .map(SerializationHelper::bytesToJsonObject)
//                .map(Object::toString)
//                .findAny()
//                .orElseThrow(() -> ClientException.notFoundError(username, User.class));
        return new ChaincodeResponseMessage(MessageStatus.NOT_IMPLEMENTED);
    }

    private ChaincodeResponseMessage create(UserFacade userFacade, List<String> params) {
        String id = UUID.randomUUID().toString();

        if (userFacade.getById(id).isPresent()) {
            throw new RuntimeException("UUID already existed");
        }

        String userString = params.get(0);
        User user = userDataConverter.deserialize(stringToJsonObject(userString), DataConverter.DeserializeView.newObjectCreationFromJson);
        userFacade.create(user);

        JsonObject jsonObject = Json.createObjectBuilder().add("id", id).build();
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);
    }

    private ChaincodeResponseMessage update(UserFacade userFacade, List<String> params) {
        String userString = params.get(0);
        User user = userDataConverter.deserialize(stringToJsonObject(userString), DataConverter.DeserializeView.updateObjectFromJson);
        user = userFacade.merge(user);
        return new ChaincodeResponseMessage(MessageStatus.OK, userDataConverter.serialize(user, DataConverter.SerializeView.objectBetweenChaincodes));
    }

    private ChaincodeResponseMessage createUser(UserFacade userFacade, List<String> params) {

        UserDataConverter userDataConverter = new UserDataConverter();
        JsonObject userJsonObject = SerializationHelper.stringToJsonObject(params.get(0));
        User user = userDataConverter.deserialize(userJsonObject, DataConverter.DeserializeView.newObjectCreationFromJson);
        user.setId(user.getUserName() + "_id");
        user = userFacade.merge(user);
        user = userFacade.merge(user);
        logger.info("[createUser] user register. # user)" + user);
        JsonObject jsonObject = userDataConverter.serialize(user, DataConverter.SerializeView.objectInFabricToJsonToApp);
        return new ChaincodeResponseMessage(MessageStatus.OK, jsonObject);

    }

    private void initUsers(UserFacade userFacade) {
        String dbsystelId = "dbs_id";
        String dbenergyId = "dbe_id";

        createUserIfNotExists(userFacade, "christian", "Christian", "Raue", dbenergyId, MarketRoleName.DEMAND_CREATOR);
        createUserIfNotExists(userFacade, "demandcreator", "Herr Demand", "Creator", dbenergyId, MarketRoleName.DEMAND_CREATOR);
        createUserIfNotExists(userFacade, "userCommAppr", "userCommAppr", "userCommAppr", dbenergyId, MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL);
        createUserIfNotExists(userFacade, "userTechAppr", "userTechAppr", "userTechAppr", dbenergyId, MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL);
        createUserIfNotExists(userFacade, "signerdbe", "signerdbe", "Ledig", dbenergyId, MarketRoleName.CUSTOMER_SIGNER);
        createUserIfNotExists(userFacade, "sonja", "Sonja", "Greve", dbsystelId, MarketRoleName.OFFER_CREATOR);
        createUserIfNotExists(userFacade, "offerAppr", "offerAppr", "offerAppr", dbsystelId, MarketRoleName.SUPPLIER_OFFER_APPROVAL);
        createUserIfNotExists(userFacade, "signerdbs", "signerdbs", "....", dbsystelId, MarketRoleName.SUPPLIER_SIGNER);
        createUserIfNotExists(userFacade, "pmdbs", "pmdbs", "....", dbsystelId, MarketRoleName.SUPPLIER_PROJECT_MANAGER);
    }

    private User createUserIfNotExists(UserFacade userFacade, String username, String firstName, String lastName, String organizationId, MarketRoleName... marketRoleNames) {
        User user = new User();
        user.setId(username + "_id");
        user.setUserName(username);
        user.setFirsName(firstName);
        user.setLastName(lastName);
        user.setOrganizationId(organizationId);
        user.setPassword("a");
        user.setMarketRole(Arrays.asList(marketRoleNames));

        user = userFacade.merge(user);
        return user;
    }

    private void createOrganization(Organization organization) {

    }

    private void addRoleToUser(MarketRoleName roleName, String userId, String organizationId) {

    }

    private void getOrganization(String organizationId) {

    }

    private UserServicesChaincodeAction getAction(String actionName) {
        return UserServicesChaincodeAction.valueOf(actionName);
    }

    private JsonObject stringToJsonObject(String input) {
        return Json.createReader(new StringReader(input)).readObject();
    }

    public static void main(String[] args) {
        new ObjectChaincode().start(args);
    }

}
