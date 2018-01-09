package de.deutschebahn.ilv.app.attachment;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.BusinessObject;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.core.GenericType;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Created by AlbertLacambraBasil on 31.07.2017.
 */
public class Attachment {

    /**
     * Format is year - day of the year - milli of the day
     */
    public static final String SEPARATOR = "---";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-D-A");
    private String objectId;
    private final String filename;
    private final String fileId;
    private Class<?> objectType;
    private InputStream documentData;
    private String timeStamp;
    private String fileHash;
    private int fileSizeInBytes;


    public Attachment(MultipartFormDataInput parts, Class<? extends BusinessObject> objectType, String objectId) {
        this.objectId = objectId;

        if (parts.getFormDataMap() == null
                || parts.getFormDataMap().get("attachment") == null
                || parts.getFormDataMap().get("attachment").get(0) == null) {
            throw ClientException.createClientException("You did not submit an attachment.");
        }

        InputPart filePart = parts.getFormDataMap().get("attachment").get(0);

        filename = findFileName(filePart);
        this.objectType = objectType;

        try {
            documentData = filePart.getBody(new GenericType<>(InputStream.class));
            this.fileSizeInBytes = documentData.available();
        } catch (IOException e) {
            throw ClientException.createClientException(e.getMessage());
        }
        fileId = generateFileId();
    }

    public Attachment(AttachmentEntity entity) {
        this(entity.getFileName());
        this.fileHash = entity.getHash();
        this.fileSizeInBytes = entity.getFileSizeInBytes();
    }

    public Attachment(String fileId) {
        String[] fileIdParts = fileId.split(SEPARATOR);
        if (fileIdParts.length != 4) {
            throw ClientException.invalidFileIdFormatError(fileId);
        }

        this.fileId = fileId;
        objectId = fileIdParts[1];
        timeStamp = fileIdParts[2];
        filename = fileIdParts[3];
    }

    /**
     * This is currently used to create fake deliveries, until we know how to deliver them.
     *
     * @param date
     */
    public Attachment(LocalDateTime date) {
        this.filename = "Delivery" + SEPARATOR + "24" + SEPARATOR + "2017-269-51874890 " + SEPARATOR + " delivery.txt";
        this.fileId = "test " + SEPARATOR + " 1";
        this.timeStamp = date.format(DATE_TIME_FORMATTER);
    }

    public InputStream getFileData() {
        return documentData;
    }

    public InputStream getDocumentData() {
        return documentData;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getFileName() {
        return filename;
    }

    public String getFileId() {
        return fileId;
    }

    private String generateFileId() {
        this.timeStamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        return objectType.getSimpleName() + SEPARATOR + objectId + SEPARATOR + timeStamp + SEPARATOR + filename;
    }

    public String getVersion() {
        return timeStamp;
    }

    private String findFileName(InputPart filePart) {
        String[] contentValues = filePart
                .getHeaders()
                .get("Content-Disposition")
                .get(0)
                .split(";");

        return Stream.of(contentValues)
                .filter(v -> v.contains("filename"))
                .map(v -> v.split("="))
                .map(v -> v[1])
                .findAny()
                .orElseThrow(() -> new RuntimeException("no filename found"))
                .replace("\"", "");
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public void setFileHash(byte[] fileHash) {
        this.fileHash = getFileHashAsString(fileHash);
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getFileHashAsString(byte[] fileHash) {

        if (fileHash == null) {
            return "";
        }

        StringBuilder digestStr = new StringBuilder();
        for (int i = 0; i < fileHash.length; i++) {
            String hex = Integer.toHexString(0xff & fileHash[i]);
            if (hex.length() == 1) digestStr.append('0');
            digestStr.append(hex);
        }
        return digestStr.toString();
    }

    public int getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(int fileSizeInByte) {
        this.fileSizeInBytes = fileSizeInByte;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "objectId=" + objectId +
                ", fileId='" + fileId + '\'' +
                ", objectType=" + objectType +
                ", timeStamp='" + timeStamp + '\'' +
                ", fileHash=" + fileHash +
                ", fileSize=" + getFileSizeInBytes() +
                '}';
    }
}
