package de.deutschebahn.ilv.app;

import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import java.util.List;
import java.util.Optional;

/**
 * Created by alacambra on 03.06.17.
 */
public class Utils {
    public static <T> Optional<T> getOnlyOneItem(List<T> objects) {
        if (!objects.isEmpty()) {
            return Optional.of(objects.get(0));
        }

        return Optional.empty();
    }

    public static Optional<ObjectStateTransitionAction> getAction(String action) {

        try {
            return Optional.of(ObjectStateTransitionAction.valueOf(action));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the rightmost len characters of a String.
     *
     * @param str
     * @param len
     * @return the last len chars of a String
     */
    public static String stringRight(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }
}