package de.deutschebahn.ilv.smartcontract.client;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.smartcontract.commons.*;
import de.deutschebahn.ilv.smartcontract.commons.model.ActionInvocation;
import org.hyperledger.fabric.sdk.ChaincodeID;

import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 14.10.2017.
 */
public class BusinessObjectClient<T extends BusinessObject> extends AbstractChaincodeClient<T> {
    public BusinessObjectClient(String loggedUserId, SmartContractClient client, DataConverter<T> dataConverter, ChaincodeID chaincodeID) {
        super(loggedUserId, client, dataConverter, chaincodeID);
    }

    public CommunicationResult<T> fireAction(ActionInvocation actionInvocation) {
        actionInvocation = new ActionInvocation(actionInvocation.getAction(), replaceToUnicode(actionInvocation.getObjectId()));
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(
                GenericActions.fireAction.name(),
                getLoggedUserId(),
                actionInvocation.asParamList(),
                ChaincodeInvocationMessage.Type.USER
        );

        ChaincodeResponseMessage message = invoke(invocationMessage);
        return wrapResult(message);
    }

    public CommunicationResult<T> attachFile(String demandId, AttachmentEntity attachmentEntity) {
        attachmentEntity.setDateCreated(new Date());
        attachmentEntity.setLastModified(new Date());
        demandId = replaceToUnicode(demandId);
        //TODO: injection
        AttachmentDataConverter attachmentDataConverter = new AttachmentDataConverter();
        JsonObject jsonObject = attachmentDataConverter.serialize(attachmentEntity, DataConverter.SerializeView.createJsonForNewObject);
        ChaincodeInvocationMessage invocationMessage = new ChaincodeInvocationMessage(
                GenericActions.attachEntity.name(),
                getLoggedUserId(),
                //TODO: pass it through model
                Arrays.asList(demandId, jsonObject.toString()),
                ChaincodeInvocationMessage.Type.USER
        );

        ChaincodeResponseMessage responseMessage = invoke(invocationMessage);
        CommunicationResult<T> result = wrapResult(responseMessage);

        return result;
    }

    @Override
    protected void removeUnicodeCompositeKeySymbol(T object) {
        object.getAttachmentEntities().stream().forEach(this::removeUnicodeCompositeKeySymbolFromAttachments);
        super.removeUnicodeCompositeKeySymbol(object);
    }

    @Override
    protected void rebuildUnicodeCompositeKeySymbol(T object) {
        object.getAttachmentEntities().stream().forEach(this::rebuildUnicodeCompositeKeySymbolFromAttachments);
        super.rebuildUnicodeCompositeKeySymbol(object);
    }

    protected AttachmentEntity removeUnicodeCompositeKeySymbolFromAttachments(AttachmentEntity att) {
        att.setOwnerObjectId(replaceToExternalSymbol(att.getOwnerObjectId()));
        att.setId(replaceToExternalSymbol(att.getId()));
        return att;
    }

    protected AttachmentEntity rebuildUnicodeCompositeKeySymbolFromAttachments(AttachmentEntity att) {
        att.setOwnerObjectId(replaceToUnicode(att.getOwnerObjectId()));
        att.setId(replaceToUnicode(att.getId()));
        return att;
    }
}
