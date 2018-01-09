package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class AttachmentEntityFacade extends ObjectFacade<AttachmentEntity> {

    @Override
    protected AbstractChaincodeClient<AttachmentEntity> getChaincodeClient() {
        return null;
    }
}
