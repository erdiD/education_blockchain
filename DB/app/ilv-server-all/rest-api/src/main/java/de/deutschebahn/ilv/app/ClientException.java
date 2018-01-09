package de.deutschebahn.ilv.app;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by alacambra on 03.06.17.
 */
@ApplicationException
public class ClientException extends WebApplicationException {

    public static ClientException createParamNotGivenException(String param) {
        return new ClientException(param + " not given", Response.Status.BAD_REQUEST);
    }

    public static ClientException createForbiddenException() {
        return new ClientException(Response.Status.FORBIDDEN);
    }

    public static ClientException createActionNotAllowedException() {
        return new ClientException(Response.Status.NOT_ACCEPTABLE);
    }

    public static ClientException createMissingHeader(String header) {
        return new ClientException(header + " not sent", Response.Status.BAD_REQUEST);
    }

    public static ClientException createInvalidValue(String key, String value) {
        return new ClientException("Invalid value " + value + " for key " + key, Response.Status.BAD_REQUEST);
    }

    public static ClientException formFieldNotFoundError(String expectedField) {
        return new ClientException("Form field " + expectedField + " not found on request");
    }

    public static ClientException invalidFileIdFormatError(String fileId) {
        return new ClientException("Invalid fileId value. " + fileId);
    }

    public static ClientException createInvalidValue() {
        return new ClientException("Invalid object sent", Response.Status.BAD_REQUEST);
    }

    public static ClientException createInvalidValue(String value) {
        return new ClientException("Invalid value sent:" + value, Response.Status.BAD_REQUEST);
    }

    public static ClientException createNotFoundError(String id, Class<?> entity) {
        return new ClientException(entity.getName() + " with " + id + " not found", Response.Status.NOT_FOUND);
    }

    public static ClientException createNotFoundContractForObjectError(String id, Class<?> entity) {
        return new ClientException("Contract of " + entity.getName() + " with " + id + " not found", Response.Status.NOT_FOUND);
    }

    public static ClientException createNotFoundError(String param, String entity) {
        return new ClientException(entity + " with " + param + " not found", Response.Status.NOT_FOUND);
    }

    public static ClientException createNotAuthorized() {
        return new ClientException("unauthorized", Response.Status.UNAUTHORIZED);
    }

    public static ClientException createClientException(String message) {
        return new ClientException(message);
    }

    private ClientException() {
    }

    private ClientException(String message) {
        super(message);
    }

    private ClientException(Response response) {
        super(response);
    }

    private ClientException(String message, Response response) {
        super(message, response);
    }

    private ClientException(int status) {
        super(status);
    }

    private ClientException(String message, int status) {
        super(message, status);
    }

    private ClientException(Response.Status status) {
        super(status);
    }

    private ClientException(String message, Response.Status status) {
        super(message, status);
    }

    public Response.Status getStatus() {
        return Response.Status.fromStatusCode(getResponse().getStatus());
    }

    @Override
    public String toString() {
        return "ClientException{" +
                " status=" + getStatus() +
                " message" + getMessage() +
                "}";
    }
}
