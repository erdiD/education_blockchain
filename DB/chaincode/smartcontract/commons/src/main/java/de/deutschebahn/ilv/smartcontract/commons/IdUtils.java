package de.deutschebahn.ilv.smartcontract.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    //TODO: Take it from single point
    public static final String EXT_SEPARATOR = "___";
    public static final String HL_SEPARATOR = "\u0000";

    public static String extractProjectId(String objectId) {
        String regex = "^(P_[\\w]{8}-[\\w]{4}-[4][\\w]{3}-[\\w]{4}-[\\w]{12})(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectId);

        if (!matcher.matches()) {
            throw new RuntimeException("[getProjectId] Invalid id passed: " + objectId);
        }

        return getMatcherForId(objectId).group(1);
    }

    public static String getDemandId(String objectId) {
        return append(extractProjectId(objectId), "D");
    }

    public static String getDeliveryId(String objectId) {
        return append(extractProjectId(objectId), "L");
    }

    public static String getContractId(String objectId) {
        return append(extractProjectId(objectId), "C");
    }

    private static String append(String id, String qualifier) {
        return id + EXT_SEPARATOR + qualifier + EXT_SEPARATOR;
    }

    private static Matcher getMatcherForId(String objectId) {
        String regex = "^(P_[\\w]{8}-[\\w]{4}-[4][\\w]{3}-[\\w]{4}-[\\w]{12})(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectId);

        if (!matcher.matches()) {
            throw new RuntimeException("[getProjectId] Invalid id passed: " + objectId);
        }

        return matcher;
    }
}