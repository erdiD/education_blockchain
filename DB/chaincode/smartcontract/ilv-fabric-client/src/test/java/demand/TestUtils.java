package demand;

import de.deutschebahn.ilv.smartcontract.client.FabricClient;
import de.deutschebahn.ilv.smartcontract.client.FabricClientFactory;
import de.deutschebahn.ilv.smartcontract.client.PropertyReader;
import de.deutschebahn.ilv.smartcontract.client.ProposalsResult;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.junit.Assert;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

public class TestUtils {

    private static final Logger logger = Logger.getLogger(TestUtils.class.getName());

    public static JsonObject getJsonObjectFromFile(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        return Json.createReader(is).readObject();
    }

    public static JsonObjectBuilder getJsonObjectBuilderFromFile(String filename) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        JsonObject object = Json.createReader(is).readObject();

        JsonObjectBuilder builder = toBuilder(object);
        return builder;
    }

    public static JsonObjectBuilder toBuilder(JsonObject object) {

        JsonObjectBuilder builder = Json.createObjectBuilder();
        object.entrySet().forEach(pair -> {
            builder.add(pair.getKey(), pair.getValue());
        });

        return builder;
    }

    public static JsonObjectBuilder getJsonObjectBuilderForProjectObjectFromFile(String filename, int demandId) {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(filename);
        JsonObject object = Json.createReader(is).readObject();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        object.getJsonObject("offer").entrySet().forEach(pair -> {
            builder.add(pair.getKey(), pair.getValue());
        });

        builder.add("projectID", demandId);

        return Json.createObjectBuilder().add("offer", builder);
    }

    public static int extractId(JsonObject jsonObject) {
        return jsonObject.getInt("id");
    }


    public static void initCC(ChaincodeID chaincodeID, Properties properties) {

        String chaincodeSourceLocation = PropertyReader.getValueOrException("chaincodeSourceLocation", properties::getProperty);
        String endorsementPolicyConfigFile = PropertyReader.getValueOrException("endorsementPolicyConfigFile", properties::getProperty);
        String channelConfigLocation = PropertyReader.getValueOrException("channelConfigLocation", properties::getProperty);
        String channelName = PropertyReader.getValueOrException("channelName", properties::getProperty);

        FabricClient fabricClient = FabricClientFactory.createFabricClient(properties);
        Channel channel = fabricClient.createOrInitChannel(channelName, channelConfigLocation);

        ProposalsResult proposalsResult = fabricClient.installChaincode(
                chaincodeID
                , chaincodeSourceLocation
                , new HashSet<>(Arrays.asList(fabricClient.getAssignedPeer()))
        );

        if (!proposalsResult.successfull()) {
            logger.warning("[initCC] proposalsResult shows error. It can be an install delay=" + proposalsResult.getMessage());
            try {
                //Wait some time to allow to finish the installation in case that the error is not real
                Thread.sleep(2000);
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
            }
        }

        proposalsResult = fabricClient.instantiateOrUpgradeChaincode(chaincodeID, channel, endorsementPolicyConfigFile);

        if (!proposalsResult.successfull()) {
//            Assert.fail(proposalsResult.getMessage());
        }
    }
}
