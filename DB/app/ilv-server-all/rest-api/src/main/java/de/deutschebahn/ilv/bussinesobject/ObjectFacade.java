package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.domain.Persistable;
import de.deutschebahn.ilv.smartcontract.client.AbstractChaincodeClient;
import de.deutschebahn.ilv.smartcontract.client.CommunicationResult;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public abstract class ObjectFacade<T extends Persistable> {

    private Logger logger = Logger.getLogger(getClass().getName() + "#" + ObjectFacade.class.getSimpleName());

    protected abstract AbstractChaincodeClient<T> getChaincodeClient();

    public Optional<T> getById(String id) {
        CommunicationResult<T> result = getChaincodeClient().getById(id);
        return checkCommunicationResultAndReturn(result);
    }

    public List<T> findAll() {
        CommunicationResult<List<T>> result = getChaincodeClient().findAll();
        checkCommunicationResult(result);
        return result.getResult();
    }

    public T merge(T object) {

        CommunicationResult<T> r;
        //TODO Update of time should be rollback in case exceptions happens. Better if object is cloned
        if (object.getId() != null) {
            object.setLastModified(new Date());
            r = getChaincodeClient().update(object);
        } else {
            object.setLastModified(new Date());
            object.setDateCreated(new Date());
            r = getChaincodeClient().create(object);
        }

        return checkCommunicationResultAndReturn(r).get();
    }

    public void remove(T object) {
        checkCommunicationResult(getChaincodeClient().remove(object.getId()));
    }

    protected void checkCommunicationResult(CommunicationResult<?> result) {

        if (result.getMessageStatus().isSuccessful()) {
            return;
        }

        switch (result.getMessageStatus()) {

            case OK:
            case NOT_FOUND:
                break;
            case UNAUTHORIZED:
                throw ClientException.createNotAuthorized();
            case FORBIDDEN:
                throw ClientException.createForbiddenException();
            case ACTION_NOT_ACCEPTED:
                throw ClientException.createActionNotAllowedException();
            case CALL_ERROR:
            case NO_METHOD_FOUND:
            case NOT_SPECIFIED_STATUS:
            case INTERNAL_ERROR:
            case NOT_IMPLEMENTED:
                logger.info("[checkCommunicationResult] Error message received # result=" + result);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected Optional<T> checkCommunicationResultAndReturn(CommunicationResult<T> result) {
        checkCommunicationResult(result);
        if (result.getMessageStatus().isSuccessful()) {
            return Optional.of(result.getResult());
        } else {
            return Optional.empty();
        }
    }

    protected String getProjectId(String objectId) {
        String regex = "^(P_[\\w]{8}-[\\w]{4}-[4][\\w]{3}-[\\w]{4}-[\\w]{12})(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectId);

        if (!matcher.matches()) {
            logger.warning("[getProjectId] Invalid id passed: " + objectId);
            throw new RuntimeException("[getProjectId] Invalid id passed: " + objectId);
        }

        return matcher.group(1);
    }
}
