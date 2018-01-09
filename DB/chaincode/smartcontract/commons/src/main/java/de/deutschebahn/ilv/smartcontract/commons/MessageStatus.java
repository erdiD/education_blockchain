package de.deutschebahn.ilv.smartcontract.commons;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public enum MessageStatus {
    OK(true), NOT_FOUND, UNAUTHORIZED, CALL_ERROR, NO_METHOD_FOUND, NOT_SPECIFIED_STATUS, FORBIDDEN,
    INTERNAL_ERROR, NOT_IMPLEMENTED, ACTION_NOT_ACCEPTED;
    boolean isSuccess;

    MessageStatus(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    MessageStatus() {
        this.isSuccess = false;
    }

    public boolean isSuccessful() {
        return isSuccess;
    }
}
