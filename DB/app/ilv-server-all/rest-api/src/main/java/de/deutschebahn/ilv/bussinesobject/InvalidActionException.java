package de.deutschebahn.ilv.bussinesobject;

import javax.ejb.ApplicationException;

/**
 * Created by alacambra on 04.06.17.
 */

@ApplicationException
public class InvalidActionException extends RuntimeException {

    public static InvalidActionException createActionNotAvailableException(String action, String state, Class<?> objectType, String id) {
        return new InvalidActionException("Not possible to execute action " + action + " over "
                + objectType.getSimpleName() + " with id " + id + ". Reason: Action not acceptable");
    }

    public static InvalidActionException createInvalidStateForActionException(String action, String state, Class<?> objectType, String id) {
        return new InvalidActionException("Not possible to execute action " + action + " over "
                + objectType.getSimpleName() + " with id " + id + ". Reason: Action can not be fired on current state");
    }

    public static InvalidActionException createNotAcceptableRolesException(String action, String state, Class<?> objectType, String id) {
        return new InvalidActionException("Not possible to execute action " + action + " over "
                + objectType.getSimpleName() + " with id " + id + ". Reason: User is not allowed to execute action");
    }

    public static InvalidActionException createPoliciesFailedException(String action, String state, Class<?> objectType, String id, String policiesNames) {
        return new InvalidActionException("Not possible to execute action " + action + " over "
                + objectType.getSimpleName() + " with id " + id + ". Reason: policies " + policiesNames + " failed");
    }

    public InvalidActionException() {
    }

    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidActionException(Throwable cause) {
        super(cause);
    }

    public InvalidActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}