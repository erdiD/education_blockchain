package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.ChaincodeName;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTarget;
import de.deutschebahn.ilv.smartcontract.commons.ClientException;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class UserPrincipalService {

    private final ChaincodeStub chaincodeStub;
    private final DataConverter<User> userDataConverter;
    private final String userId;
    private User user;

    public UserPrincipalService(ChaincodeStub chaincodeStub, DataConverter<User> userDataConverter, String userId) {
        this.chaincodeStub = chaincodeStub;
        this.userDataConverter = userDataConverter;
        this.userId = userId;
    }

    public User loadUser() {
        if (user == null) {
            user = new ChaincodeTarget(ChaincodeName.USER_SERVICE_CC)
                    .withChaincodeStub(chaincodeStub)
                    .function(GenericActions.getById.name())
                    .asUser(userId)
                    .build()
                    .execute(jsonObject ->
                            userDataConverter.deserialize(jsonObject, DataConverter.DeserializeView.objectBetweenChaincodes)
                    ).orElseThrow(() -> ClientException.notFoundError(userId, User.class));
        }
        return user;
    }

    public User getUser() {
        return user;
    }

    public interface UserPrincipalServiceFactory{
        UserPrincipalService getUserPrincipalService(ChaincodeStub chaincodeStub, DataConverter<User> userDataConverter, String userId);
    }
}
