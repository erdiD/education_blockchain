package de.deutschebahn.ilv.bussinesobject.blockchain;

/**
 * Created by AlbertLacambraBasil on 06.07.2017.
 */
public class InvalidBlockchainIdException extends RuntimeException {

    public InvalidBlockchainIdException() {
    }

    public InvalidBlockchainIdException(String message) {
        super(message);
    }

    public InvalidBlockchainIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBlockchainIdException(Throwable cause) {
        super(cause);
    }

    public InvalidBlockchainIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
