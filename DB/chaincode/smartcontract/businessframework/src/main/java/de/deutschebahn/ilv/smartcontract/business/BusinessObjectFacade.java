package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.commons.AttachmentDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class BusinessObjectFacade<T extends BusinessObject> extends ObjectFacade<T> {

    private final AttachmentFacade attachmentFacade;
    private final Logger logger = Logger.getLogger(getClass().getName() + "#" + BusinessObjectFacade.class.getSimpleName());

    protected BusinessObjectFacade(DataConverter<T> dataConverter, ChaincodeStub chaincodeStub) {
        super(dataConverter, chaincodeStub);
        //TODO injection
        AttachmentDataConverter attachmentDataConverter = new AttachmentDataConverter();
        attachmentFacade = new AttachmentFacade(attachmentDataConverter, chaincodeStub);
    }

    public List<AttachmentEntity> findAllAttachments(String objectId) {
        return attachmentFacade.findAll(objectId);
    }

    public AttachmentEntity saveAttachment(String ownerObjectId, AttachmentEntity attachment) {
        attachment.setOwnerObjectId(ownerObjectId);
        return attachmentFacade.create(attachment);
    }

    public T prepareObject(T object, AvailableActionsService availableActionsService, User user) {
        List<AttachmentEntity> entities = findAllAttachments(object.getId());
        object.setAttachmentEntities(new ArrayList<>(entities));
        Collection<String> actions = availableActionsService.getAvailableActions(user, object);
        object.setAvailableActions(new ArrayList<>(actions));
        return object;
    }
}
