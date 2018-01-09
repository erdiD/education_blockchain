package de.deutschebahn.ilv.smartcontract.commons;

import static de.deutschebahn.ilv.smartcontract.commons.MessageStatus.*;

/**
 * Created by alacambra on 03.06.17.
 */
public class ClientException extends RuntimeException {

    private MessageStatus status = NOT_SPECIFIED_STATUS;

    public static ClientException paramNotGivenException(String param) {
        return new ClientException(param + " not given", CALL_ERROR);
    }

    public static ClientException forbiddenException() {
        return new ClientException(FORBIDDEN);
    }

    public static ClientException missingHeader(String header) {
        return new ClientException(header + " not sent", CALL_ERROR);
    }

    public static ClientException invalidValue(String key, String value) {
        return new ClientException("Invalid value '" + value + "' for key " + key, CALL_ERROR);
    }

    public static ClientException formFieldNotFoundError(String expectedField) {
        return new ClientException("Form field " + expectedField + " not found on request");
    }

    public static ClientException invalidFileIdFormatError(String fileId) {
        return new ClientException("Invalid fileId value. " + fileId);
    }

    public static ClientException invalidValue() {
        return new ClientException("Invalid object sent", CALL_ERROR);
    }

    public static ClientException invalidValue(String value) {
        return new ClientException("Invalid value sent:" + value, CALL_ERROR);
    }

    public static ClientException notFoundError(String id, Class<?> entity) {
        return new ClientException(entity.getName() + " with id " + id + " not found", NOT_FOUND);
    }

    public static ClientException notFoundContractForObjectError(Long id, Class<?> entity) {
        return new ClientException("Contract of " + entity.getName() + " with " + id + " not found", NOT_FOUND);
    }

    public static ClientException notFoundError(String param, String entity) {
        return new ClientException(entity + " with " + param + " not found", NOT_FOUND);
    }

    public static ClientException notAuthorized() {
        return new ClientException("unauthorized", UNAUTHORIZED);
    }

    public static ClientException createClientException(String message) {
        return new ClientException(message);
    }

    private ClientException() {
    }

    private ClientException(String message) {
        super(message);
    }

    private ClientException(String message, MessageStatus status) {
        super(message);
        this.status = status;
    }


    private ClientException(MessageStatus status) {
        this.status = status;
    }

    private ClientException(Throwable cause) {
        super(cause);
    }

    private ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    private ClientException(Throwable cause, MessageStatus status) throws IllegalArgumentException {
        super(cause);
        this.status = status;
    }

    private ClientException(String message, Throwable cause, MessageStatus status) throws IllegalArgumentException {
        super(message, cause);
        this.status = status;
    }

    public MessageStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "status=" + status +
                ", message=" + getMessage() +
                '}';
    }
}
