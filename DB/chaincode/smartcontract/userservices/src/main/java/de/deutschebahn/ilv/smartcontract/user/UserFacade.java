package de.deutschebahn.ilv.smartcontract.user;

import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.ObjectFacade;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class UserFacade extends ObjectFacade<User> {
    public UserFacade(UserDataConverter userDataConverter, ChaincodeStub chaincodeStub) {
        super(userDataConverter, chaincodeStub);
    }

    @Override
    protected void checkId(String objectId) {
    }
}