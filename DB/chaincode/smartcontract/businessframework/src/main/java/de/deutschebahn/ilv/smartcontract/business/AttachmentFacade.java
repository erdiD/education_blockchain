package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTarget;
import de.deutschebahn.ilv.smartcontract.commons.AttachmentDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import de.deutschebahn.ilv.smartcontract.commons.model.ProjectField;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 21.10.2017.
 */
public class AttachmentFacade extends ObjectFacade<AttachmentEntity> {

    public AttachmentFacade(DataConverter<AttachmentEntity> dataConverter, ChaincodeStub chaincodeStub) {
        super(dataConverter, chaincodeStub);
    }

    public List<AttachmentEntity> findAll(String objectId) {
        //TODO injection
        AttachmentDataConverter attachmentDataConverter = new AttachmentDataConverter();
        String attId = IdUtils.recreateAttachmentsId(objectId);

        QueryResultsIterator<KeyValue> keyValues = chaincodeStub.getStateByPartialCompositeKey(attId);
        ArrayList<AttachmentEntity> objects = new ArrayList<>();

        for (KeyValue keyValue : keyValues) {
            JsonObject jsonObject = stringToJsonObject(new String(keyValue.getValue()));
            AttachmentEntity object = attachmentDataConverter.deserialize(jsonObject, DataConverter.DeserializeView.jsonInDatabaseToObjectInFabric);
            objects.add(object);
        }

        return objects;
    }

    @Override
    public AttachmentEntity create(AttachmentEntity object) {

        String attId = getNextAttachmentId(object.getOwnerObjectId());
        object.setId(attId);

        return super.create(object);
    }

    private String getNextAttachmentId(String objectId) {
        int counter = new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(getChaincodeStub())
                .function(GenericActions.getNextId.name())
                .params(AttachmentEntity.class.getSimpleName(), objectId)
                .asPeer()
                .build()
                .execute(jsonObject -> Integer.parseInt(new ProjectField(jsonObject).getValue())).orElse(-1);

        return IdUtils.generateAttachmentKey(objectId, counter).toString();
    }
}
