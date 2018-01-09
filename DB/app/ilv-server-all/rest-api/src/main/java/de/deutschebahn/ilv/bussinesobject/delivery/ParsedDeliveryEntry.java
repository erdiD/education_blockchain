package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.bussinesobject.DateHelper;
import de.deutschebahn.ilv.domain.DeliveryEntry;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class ParsedDeliveryEntry {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String line;

    private String psp;
    private LocalDate date;
    private BigDecimal paymentValue;
    protected boolean isValid = true;
    protected String errorMsg;
    
    private static final Logger logger = Logger.getLogger(ParsedDeliveryEntry.class.getName());

    public ParsedDeliveryEntry(Collection<String> validPsps, String line) {
        Objects.requireNonNull(line);
        
        this.line = line;
        String[] parts = line.split("#");
        load(validPsps, parts);
    }

    protected ParsedDeliveryEntry(String line) {
        Objects.requireNonNull(line);
        this.line = line;
    }

    protected void load(Collection<String> validPsps, String[] parts) {
        if (!partsNumberIsValid(parts)) {
            return;
        }

        psp = parts[0];

        if (!validPsps.contains(psp)) {
            isValid = false;
            errorMsg = String.format("psp %s is not valid. Valid psps are %s", psp, validPsps.toString());
            return;
        }

        if (!loadDateIfValid(parts[1]) || !loadPaymentValueIfValid(parts[2])) {
            return;
        }
    }

    private boolean loadDateIfValid(String dateString) {
        try {
            date = LocalDate.from(DATE_FORMATTER.parse(dateString));
        } catch (DateTimeParseException e) {
            isValid = false;
            errorMsg = "Error of type " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        return isValid;
    }

    private boolean loadPaymentValueIfValid(String candidate) {
        try {
            DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMANY);
            formatter.applyLocalizedPattern("#.#00,0#");
            formatter.setParseBigDecimal(true);
            paymentValue = (BigDecimal) formatter.parse(candidate);
        } catch (NumberFormatException | ParseException e) {
            isValid = false;
            errorMsg = "Error of type " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }

        return isValid;
    }

    public String getLine() {
        return line;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getPsp() {
        return psp;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getPaymentValue() {
        return paymentValue;
    }

    public boolean isValid() {
        return isValid;
    }

    public DeliveryEntry buildDeliveryEntry() {
        DeliveryEntry deliveryEntry = new DeliveryEntry();
        deliveryEntry.setPspId(this.getPsp());
        deliveryEntry.setPaymentValue(this.getPaymentValue());
        deliveryEntry.setDeliveryDate(DateHelper.toDate(this.getDate()));
        deliveryEntry.setLine(this.getLine());

        return deliveryEntry;
    }

    protected boolean partsNumberIsValid(String[] parts) {
        if (parts.length != 3) {
            isValid = false;
            errorMsg = String.format("invalid number of parts. Found %d instead %d. %s", parts.length, 3, Arrays.asList(parts));
            return false;
        }

        return true;
    }
}
