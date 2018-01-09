package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.AttachmentEntity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.getValueOrException;
import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.serializeValueOrException;


/**
 * Created by alacambra on 04.06.17.
 */
public class AttachmentDataConverter implements DataConverter<AttachmentEntity> {

    @Override
    public JsonObject serialize(AttachmentEntity attachmentEntity, SerializeView view) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        serializeValueOrException(builder::add, "hash", attachmentEntity.getHash());
        serializeValueOrException(builder::add, "fileName", attachmentEntity.getFileName());
        serializeValueOrException(builder::add, "ownerObjectId", attachmentEntity.getOwnerObjectId());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "dateCreated", attachmentEntity.getDateCreated());
        serializeValueOrException(builder::add, SerializationHelper::convertFromDate, "lastModified", attachmentEntity.getLastModified());
        serializeValueOrException(builder::add, "fileSizeInBytes", attachmentEntity.getFileSizeInBytes());

        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectInFabricToJsonToApp:
            case objectBetweenChaincodes:
                serializeValueOrException(builder::add, "id", attachmentEntity.getId());
                break;
        }

        return builder.build();
    }

    @Override
    public AttachmentEntity deserialize(JsonObject jsonObject, DeserializeView view) {
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setHash((String) getValueOrException("hash", jsonObject::getString));
        attachmentEntity.setFileName(getValueOrException("fileName", jsonObject::getString));
        attachmentEntity.setOwnerObjectId(getValueOrException("ownerObjectId", jsonObject::getString));
        attachmentEntity.setDateCreated(getValueOrException("dateCreated", jsonObject::getString, SerializationHelper::convertToDate));
        attachmentEntity.setLastModified(getValueOrException("lastModified", jsonObject::getString, SerializationHelper::convertToDate));
        attachmentEntity.setFileSizeInBytes(getValueOrException("fileSizeInBytes", jsonObject::getInt));

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case updateObjectFromJson:
            case jsonFromFabricToObjectInApp:
            case jsonInDatabaseToObjectInFabric:
            case objectBetweenChaincodes:
                attachmentEntity.setId(getValueOrException("id", jsonObject::getString));
                break;
        }

        return attachmentEntity;
    }

    @Override
    public String getAssignedType() {
        return AttachmentEntity.class.getSimpleName();
    }


}
