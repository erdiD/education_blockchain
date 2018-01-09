package de.deutschebahn.ilv.smartcontract.business;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * ID formats where '#' represents compositeKey part (normally \u0000)
 * Project ID:         P_GUID
 * Demand ID:          P_GUID#D
 * Offer ID:           P_GUID#O#(1..999)
 * Contract ID:        P_GUID#O#N#C where N is within (1..999) and it represents the winning Offer ID
 * Delivery ID:        P_GUID#O#N#L where N is within (1..999) and it represents the winning Offer ID
 * Delivery Item ID:   P_GUID#O#N#I#1..999 where N is within (1..999) and it represents the winning Offer ID
 * Payment Item ID:    P_GUID#O#N#Y#1..999 where N is within (1..999) and it represents the winning Offer ID
 * AttachmentItem      {BELONGING_OBJECT_ID}#A#1..999
 */
public class IdUtils {
    public static byte[] intToBytes(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).asIntBuffer().get();
    }

    public static String createProjectId() {
        return "P_" + UUID.randomUUID().toString();
    }

    public static CompositeKey generateDemandKey() {
        return new CompositeKey(createProjectId(), "D");
    }

    public static CompositeKey generateOfferKey(CompositeKey compositeKey, int counter) {
        return createNewKey(compositeKey, "O", String.valueOf(counter));
    }

    public static CompositeKey generateContractKey(CompositeKey offerId) {
        return createNewSubKey(offerId, "C");
    }

    public static CompositeKey generateAttachmentKey(CompositeKey owningObjectKey, int counter) {
        return createNewSubKey(owningObjectKey, "A", String.valueOf(counter));
    }

    public static CompositeKey generateAttachmentKey(String owningObjectId, int counter) {
        CompositeKey owningObjectKey = stringToCompositeKey(owningObjectId);
        return generateAttachmentKey(owningObjectKey, counter);
    }

    public static CompositeKey generateDeliveryKey(CompositeKey projectKey) {
        return createNewSubKey(projectKey, "L");
    }

    public static CompositeKey generateDeliveryEntryKey(CompositeKey projectKey, int counter) {
        return createNewSubKey(projectKey, "I", String.valueOf(counter));
    }

    public static CompositeKey generateDeliveryEntryKey(String projectId, int counter) {
        CompositeKey projectKey = stringToCompositeKey(projectId);
        return generateDeliveryEntryKey(projectKey, counter);
    }

    public static CompositeKey generatePaymentKey(CompositeKey offerId, int counter) {
        return createNewSubKey(offerId, "Y_" + String.valueOf(counter));
    }

    public static CompositeKey getFieldKey(String projectId, String field) {
        return new CompositeKey(projectId, field);
    }

    public static String recreateOfferId(String projectId) {
        return new CompositeKey(projectId, "O").toString();
    }

    public static String recreateDeliveryEntryId(String projectId) {
        return new CompositeKey(projectId, "I").toString();
    }

    public static String recreateAttachmentsId(String objectId) {
        SplittedKey splittedKey = new SplittedKey(objectId);
        return splittedKey.addAttribute("A").asString();
    }

    public static String recreateContractId(String projectId) {
        return new CompositeKey(projectId, "C").toString();
    }

    public static String recreateDeliveryId(String projectId) {
        return new CompositeKey(projectId, "L").toString();
    }

    public static String recreateDemandId(String projectId) {
        return new CompositeKey(projectId, "D").toString();
    }

    public static CompositeKey stringToCompositeKey(String key) {
        SplittedKey splittedKey = new SplittedKey(key);
        return splittedKey.asCompositeKey();
    }

    public static String getProjectId(CompositeKey objectKey) {
        return objectKey.getObjectType();
    }

    private static CompositeKey createNewKey(CompositeKey originalId, String... toAppend) {
        String uuid = originalId.getObjectType();
        return new CompositeKey(uuid, toAppend);
    }

    private static CompositeKey createNewSubKey(CompositeKey originalId, String... toAppend) {
        String uuid = originalId.getObjectType();
        List<String> attr = new ArrayList<>(originalId.getAttributes());
        attr.addAll(Arrays.asList(toAppend));
        return new CompositeKey(uuid, attr);
    }
}