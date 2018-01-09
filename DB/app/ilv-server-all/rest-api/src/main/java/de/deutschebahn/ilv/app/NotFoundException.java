package de.deutschebahn.ilv.app;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by alacambra on 03.06.17.
 */
public class NotFoundException extends WebApplicationException {


    public static NotFoundException createParamNotGivenException(String objectName, int id) {
        return new NotFoundException(objectName + " with id " + id + " not found");
    }

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Response response) {
        super(response);
    }

    public NotFoundException(String message, Response response) {
        super(message, response);
    }

    public NotFoundException(int status) {
        super(status);
    }

    public NotFoundException(String message, int status) {
        super(message, status);
    }

    public NotFoundException(Response.Status status) {
        super(status);
    }

    public NotFoundException(String message, Response.Status status) {
        super(message, status);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause, Response response) {
        super(cause, response);
    }

    public NotFoundException(String message, Throwable cause, Response response) {
        super(message, cause, response);
    }

    public NotFoundException(Throwable cause, int status) {
        super(cause, status);
    }

    public NotFoundException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }

    public NotFoundException(Throwable cause, Response.Status status) throws IllegalArgumentException {
        super(cause, status);
    }

    public NotFoundException(String message, Throwable cause, Response.Status status) throws IllegalArgumentException {
        super(message, cause, status);
    }
}
