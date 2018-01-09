package de.deutschebahn.ilv.flow;

import de.deutschebahn.ilv.UserRestClient;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alacambra on 03.06.17.
 */
public abstract class ILVScenarioIT {

    protected static Map<MarketRoleName, UserRestClient> registeredUsers = new HashMap<>();

    private static final String END_POINT_LOCAL = "http://localhost:8080/ilv/resources/";
    private static final String END_POINT_MATILDA = "http://matilda.dbe.aws.db.de:8080/ilv/resources/";
    private static final String END_POINT = END_POINT_LOCAL;
    protected static UserRestClient anotherDemandCreator;

    private static String getEndPoint() {
        return "http://" + getHostAddress() + "/ilv/resources/";
    }

    private static String getHostAddress() {
        String address = System.getenv("RESTAPIHOST");
        if (address == null || address.isEmpty() || address.matches("")) {
            address = "localhost:8080";
        }
        return address;
    }

    @BeforeClass
    public static void setUp() {

        anotherDemandCreator = registerUser("demandcreator", MarketRoleName.DEMAND_CREATOR);
        anotherDemandCreator.connect();
        registerUser("christian", MarketRoleName.DEMAND_CREATOR);
        registerUser("userCommAppr", MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL);
        registerUser("userTechAppr", MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL);
        registerUser("signerdbe", MarketRoleName.CUSTOMER_SIGNER);

        registerUser("sonja", MarketRoleName.OFFER_CREATOR);
        registerUser("offerAppr", MarketRoleName.SUPPLIER_OFFER_APPROVAL);
        registerUser("signerdbs", MarketRoleName.SUPPLIER_SIGNER);
        registerUser("pmdbs", MarketRoleName.SUPPLIER_PROJECT_MANAGER);

        registeredUsers.values().forEach(UserRestClient::connect);
    }

    private static UserRestClient registerUser(String username, MarketRoleName roleName) {
        registeredUsers.put(roleName, new UserRestClient(username, getEndPoint()));
        return registeredUsers.get(roleName);
    }

    @AfterClass
    public static void tearDown() {
        registeredUsers.values().forEach(UserRestClient::close);
        anotherDemandCreator.close();
    }

    public static UserRestClient getRestClient(MarketRoleName roleName) {
        return registeredUsers.get(roleName);
    }

    public static WebTarget getWebTarget(MarketRoleName roleName) {
        return registeredUsers.get(roleName).getWebTarget();
    }
}
