package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeResponseMessage;

import java.util.List;

/**
 * Created by AlbertLacambraBasil on 03.11.2017.
 */
public interface InterChaincodeCommunication<T extends BusinessObject> {
    void loadHandlers(RequestDependencies<T> dependencies);

    ChaincodeResponseMessage handleObjectUpdated(RequestDependencies<T> requestDependencies, List<String> params);

    void registerObjectTypesToBeNotified(ObjectUpdatedNotifier<T> objectUpdatedNotifier);
}
