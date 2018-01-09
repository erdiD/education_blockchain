package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.domain.Delivery;

import javax.ejb.ApplicationException;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
@ApplicationException
public class NoPspForDeliveryFoundException extends RuntimeException {

    public static NoPspForDeliveryFoundException noPspsFound(Delivery delivery) {
        return new NoPspForDeliveryFoundException("Psps are not set for delivery " + delivery.getId());
    }

    public NoPspForDeliveryFoundException() {
    }

    public NoPspForDeliveryFoundException(String message) {
        super(message);
    }

    public NoPspForDeliveryFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPspForDeliveryFoundException(Throwable cause) {
        super(cause);
    }

    public NoPspForDeliveryFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
