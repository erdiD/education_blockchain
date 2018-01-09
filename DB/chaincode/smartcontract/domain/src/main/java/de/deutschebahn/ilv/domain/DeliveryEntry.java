package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 10.08.2017.
 */
public class DeliveryEntry implements Persistable {

    private String id;
    private String deliveryId;
    private String attachmentEntityId;
    private String userDelivererId;
    private String line;
    private String pspId;
    private BigDecimal achievedScope;
    private BigDecimal paymentValue;
    private Date creationDate;
    private Date deliveryDate;
    private String fileName;
    private String hash;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setCreationDate() {
        creationDate = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPspId() {
        return pspId;
    }

    public void setPspId(String pspId) {
        this.pspId = pspId;
    }

    public BigDecimal getAchievedScope() {
        return achievedScope;
    }

    public void setAchievedScope(BigDecimal achievedScope) {
        this.achievedScope = achievedScope;
    }

    public BigDecimal getPaymentValue() {
        return paymentValue;
    }

    public void setPaymentValue(BigDecimal paymentValue) {
        this.paymentValue = paymentValue;
    }

    public String getLine() {
        return line;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getAttachmentEntityId() {
        return attachmentEntityId;
    }

    public void setAttachmentEntityId(String attachmentEntityId) {
        this.attachmentEntityId = attachmentEntityId;
    }

    public String getUserDelivererId() {
        return userDelivererId;
    }

    public void setUserDelivererId(String userDelivererId) {
        this.userDelivererId = userDelivererId;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public Date getDateCreated() {
        return creationDate;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        creationDate = dateCreated;
    }

    @Override
    public Date getLastModified() {
        return creationDate;
    }

    @Override
    public void setLastModified(Date lastModified) {
        
    }

    @Override
    public String toString() {
        return "DeliveryEntry{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", deliveryDate=" + deliveryDate +
                ", line='" + line + '\'' +
                ", pspId='" + pspId + '\'' +
                ", achievedScope=" + achievedScope +
                ", paymentValue=" + paymentValue +
                '}';
    }
}
