package de.deutschebahn.ilv.smartcontract.client.user;

import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeInvocationMessage;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.UserServicesChaincodeAction;
import org.hyperledger.fabric.sdk.ChaincodeID;

import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class UserServiceChaincodeClient extends AbstractChaincodeClient<User> {
    public UserServiceChaincodeClient(String userId, SmartContractClient client, DataConverter<User> dataConverter, ChaincodeID chaincodeID) {
        super(userId, client, dataConverter, chaincodeID);
    }

    public void initUsers() {
        ChaincodeInvocationMessage invocationMessage =
                new ChaincodeInvocationMessage("initUsers", getLoggedUserId(), Collections.emptyList(), ChaincodeInvocationMessage.Type.USER);
        invoke(invocationMessage);
    }

    public void registerUser(User user) {

        JsonObject jsonObject = new UserDataConverter().serialize(user, DataConverter.SerializeView.createJsonForNewObject);

        ChaincodeInvocationMessage invocationMessage =
                new ChaincodeInvocationMessage(UserServicesChaincodeAction.regiserUser.name()
                        , getLoggedUserId()
                        , Arrays.asList(jsonObject.toString())
                        , ChaincodeInvocationMessage.Type.USER);

        invoke(invocationMessage);
    }
}