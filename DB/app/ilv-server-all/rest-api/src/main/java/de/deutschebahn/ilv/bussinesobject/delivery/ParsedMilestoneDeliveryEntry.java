package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.domain.DeliveryEntry;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class ParsedMilestoneDeliveryEntry extends ParsedDeliveryEntry {

    private static final Logger logger = Logger.getLogger(ParsedMilestoneDeliveryEntry.class.getName());

    private BigDecimal achievedScope;

    public ParsedMilestoneDeliveryEntry(Collection<String> validPsps, String line) {
        super(line);
        
        String[] parts = line.split("#");
        load(validPsps, parts);

        if (!partsNumberIsValid(parts)) {
            return;
        }

        loadAchievedScopeIfValid(parts[3]);
    }


    private boolean loadAchievedScopeIfValid(String candidate) {
        try {
            achievedScope = new BigDecimal(candidate);
        } catch (NumberFormatException e) {
            isValid = false;
            errorMsg = "Error of type " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        if (achievedScope.compareTo(new BigDecimal(100)) == 1) {
            isValid = false;
            errorMsg = "value is bigger than 100: " + candidate;
        }

        return isValid;
    }

    public BigDecimal getAchievedScope() {
        return achievedScope;
    }

    @Override
    public DeliveryEntry buildDeliveryEntry() {
        DeliveryEntry deliveryEntry = super.buildDeliveryEntry();
        deliveryEntry.setAchievedScope(this.getAchievedScope());

        return deliveryEntry;
    }

    @Override
    protected boolean partsNumberIsValid(String[] parts) {
        if (parts.length != 4) {
            isValid = false;
            errorMsg = String.format("invalid number of parts. Found %d instead %d. %s", parts.length, 4, Arrays.asList(parts));
            return false;
        }
        return true;
    }
}
