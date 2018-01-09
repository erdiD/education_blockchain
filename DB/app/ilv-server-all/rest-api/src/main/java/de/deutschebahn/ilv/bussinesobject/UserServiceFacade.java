package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.user.UserServiceChaincodeClient;

import javax.inject.Inject;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
public class UserServiceFacade extends ObjectFacade<User> {

    @Inject
    UserServiceChaincodeClient userServiceChaincodeClient;

    @Override
    protected AbstractChaincodeClient<User> getChaincodeClient() {
        return userServiceChaincodeClient;
    }
}
