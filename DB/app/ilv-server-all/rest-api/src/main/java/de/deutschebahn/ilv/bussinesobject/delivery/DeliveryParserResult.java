package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.DeliveryEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DeliveryParserResult {

    private final List<DeliveryEntry> deliveryEntries;
    private final List<DeliveryEntryError> errors;

    //TODO: persisted staff with ID. Should replace other entries. Recheck Design
    private AttachmentEntity attachmentEntity;
    private List<DeliveryEntry> persistedDeliveryEntries;


    public DeliveryParserResult() {
        this.deliveryEntries = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public DeliveryParserResult(List<DeliveryEntryError> deliveryEntryErrors) {
        this();
        errors.addAll(deliveryEntryErrors);
    }

    public AttachmentEntity getAttachmentEntity() {
        return attachmentEntity;
    }

    public void setAttachmentEntity(AttachmentEntity attachmentEntity) {
        this.attachmentEntity = attachmentEntity;
    }

    public List<DeliveryEntry> getPersistedDeliveryEntries() {
        return persistedDeliveryEntries;
    }

    public void setPersistedDeliveryEntries(List<DeliveryEntry> persistedDeliveryEntries) {
        this.persistedDeliveryEntries = persistedDeliveryEntries;
    }

    public void addDeliveryEntry(DeliveryEntry deliveryEntry) {
        deliveryEntries.add(deliveryEntry);
    }

    public void addError(DeliveryEntryError error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<DeliveryEntryError> getErrors() {
        return errors;
    }

    public List<DeliveryEntry> getDeliveryEntries() {
        return deliveryEntries;
    }

    @Override
    public String toString() {
        return "DeliveryParserResult{" +
                "deliveryEntries=" + deliveryEntries +
                ", errors=" + errors +
                '}';
    }
}
