package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.commons.model.ActionInvocation;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public abstract class BusinessObjectFacade<T extends BusinessObject> extends ObjectFacade<T> {

    private Logger logger = Logger.getLogger(getClass().getName() + "#" + BusinessObjectFacade.class.getSimpleName());

    public BusinessObjectFacade() {
    }

    public T fireAction(String objectId, ObjectStateTransitionAction action, String message) {
        return checkCommunicationResultAndReturn(getBusinessObjectClient().fireAction(new ActionInvocation(action, objectId))).get();
    }

    public T fireAction(String objectId, ObjectStateTransitionAction action) {
        return checkCommunicationResultAndReturn(getBusinessObjectClient().fireAction(new ActionInvocation(action, objectId))).get();
    }

    public T saveAttachment(AttachmentEntity attachmentEntity) {
        attachmentEntity.setDateCreated(new Date());
        attachmentEntity.setLastModified(new Date());
        return getBusinessObjectClient().attachFile(attachmentEntity.getOwnerObjectId(), attachmentEntity).getResult();
    }

    protected abstract BusinessObjectClient<T> getBusinessObjectClient();

}