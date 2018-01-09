package de.deutschebahn.ilv.app.attachment;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.json.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 01.08.2017.
 */
public class AttachmentDataConverter {
    public JsonObject toJson(Attachment attachment) {
        return Json.createObjectBuilder()
                .add("fileId", attachment.getFileId())
                .add("hash", attachment.getFileHash())
                .add("version", attachment.getVersion())
                .add("size", attachment.getFileSizeInBytes())
                .build();
    }

    public JsonObject toJson(Collection<AttachmentEntity> attachmentEntities) {

        if (attachmentEntities == null) {
            attachmentEntities = Collections.emptyList();
        }

        Collection<Attachment> attachments = attachmentEntities
                .stream()
                .map(Attachment::new)
                .collect(Collectors.toList());

        Map<String, List<JsonObject>> groupedAttachments = attachments
                .stream()
                .collect(Collectors.groupingBy(Attachment::getFileName, Collectors.mapping(this::toJson, Collectors.toList())));

        JsonObjectBuilder allFilesBuilder = Json.createObjectBuilder();

        for (Map.Entry<String, List<JsonObject>> entry : groupedAttachments.entrySet()) {
            JsonArray jsonArray = SerializationHelper.serializeToJsonArrayWithJsonObjects(entry.getValue());
            allFilesBuilder.add(entry.getKey(), jsonArray);
        }

		return allFilesBuilder.build();
	}

	/**
	 *
	 * Creates a JsonArray like this one:
	 * (sorted by Filename)
	 * <pre>
		"attachments": [{
			"delivery.txt": [{
				"fileId": "Demand_33_2017-289-52223061_delivery.txt",
				"hash": "2feca2a40658c315ee592eab0a242aca188ff7cb8e2a1e9caa34492f796af93b",
				"version": "2017-289-52223061",
				"size": 631
			}]
		}];
	 * </pre>
	 *
	 * @param attachmentEntities
	 * @return
	 */
	public JsonArray toJsonArray(Collection<AttachmentEntity> attachmentEntities) {

        if (attachmentEntities == null) {
            attachmentEntities = Collections.emptyList();
        }

        Collection<Attachment> attachments = attachmentEntities
                .stream()
                .map(Attachment::new)
                .collect(Collectors.toList());

        // sort Attachments by name and create a Map like:
        // "FILENAME" : [{Array of AttachmentEntities as JsonObjects}]
        Map<String, List<JsonObject>> groupedAttachments = attachments
                .stream()
                .collect(Collectors.groupingBy(Attachment::getFileName, Collectors.mapping(this::toJson, Collectors.toList())));

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Map.Entry<String, List<JsonObject>> entry : groupedAttachments.entrySet()) {
            JsonArray jsonArray = SerializationHelper.serializeToJsonArrayWithJsonObjects(entry.getValue());
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            arrayBuilder.add( objectBuilder.add(entry.getKey(), jsonArray) );
        }

        return arrayBuilder.build();
    }
}
