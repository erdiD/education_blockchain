package de.deutschebahn.ilv.domain;

import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 01.08.2017.
 */
public class AttachmentEntity implements Persistable {

    private String id;
    private String hash;
    private String fileName;
    private Date lastModified;
    private Date dateCreated;
    private int fileSizeInBytes;
    private String ownerObjectId;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public String getOwnerObjectId() {
        return ownerObjectId;
    }

    public void setOwnerObjectId(String ownerObjectId) {
        this.ownerObjectId = ownerObjectId;
    }

    public void setHash(byte[] hash) {
        this.hash = getHashAsString(hash);
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(int fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    private String getHashAsString(byte[] hash) {

        if (hash == null) {
            return "";
        }

        StringBuilder digestStr = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) digestStr.append('0');
            digestStr.append(hex);
        }
        return digestStr.toString();
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}

