package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.client.CommunicationResult;
import de.deutschebahn.ilv.smartcontract.client.user.UserServiceChaincodeClient;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
@Singleton
public class UsersProvider {

    private final Map<String, User> users;

    @Inject
    UserServiceChaincodeClient userServiceChaincodeClient;

    //TODO: inject using CDI
    UserDataConverter userDataConverter = new UserDataConverter();

    public UsersProvider() {
        this.users = new ConcurrentHashMap<>();
    }

    public User getUser(String id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }

        CommunicationResult<User> result = userServiceChaincodeClient.getById(id);
        if (!result.getMessageStatus().isSuccessful()) {
            throw ClientException.createNotFoundError(id, User.class);
        }

        return users.computeIfAbsent(id, k -> result.getResult());
    }
}
