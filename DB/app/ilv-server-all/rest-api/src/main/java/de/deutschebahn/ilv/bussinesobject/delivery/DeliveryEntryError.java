package de.deutschebahn.ilv.bussinesobject.delivery;

import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DeliveryEntryError {

    private String line;
    private String message;

    public DeliveryEntryError(String line, String message) {

        Objects.requireNonNull(message);
        Objects.requireNonNull(line);

        this.line = line;
        this.message = message;
    }

    public String getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DeliveryEntryError{" +
                "line='" + line + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
