package de.deutschebahn.ilv.smartcontract.client;

import de.deutschebahn.ilv.smartcontract.commons.ErrorPayload;
import de.deutschebahn.ilv.smartcontract.commons.MessageStatus;

/**
 * Created by AlbertLacambraBasil on 17.10.2017.
 */
public class CommunicationResult<T> {

    private MessageStatus messageStatus;
    private T result;
    private ErrorPayload errorPayload;

    private CommunicationResult(MessageStatus status, T object) {
        messageStatus = status;
        result = object;
    }

    private CommunicationResult(MessageStatus messageStatus, ErrorPayload errorPayload) {
        this.messageStatus = messageStatus;
        this.errorPayload = errorPayload;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public T getResult() {
        return result;
    }

    public ErrorPayload getErrorPayload() {
        return errorPayload;
    }

    public static <T> CommunicationResult<T> fail(MessageStatus status, ErrorPayload errorPayload) {
        return new CommunicationResult<T>(status, errorPayload);
    }

    public static <T> CommunicationResult<T> success(MessageStatus status, T object) {
        return new CommunicationResult<T>(status, object);
    }

    @Override
    public String toString() {
        return "CommunicationResult{" +
                "messageStatus=" + messageStatus +
                ", result=" + result +
                ", errorPayload=" + errorPayload +
                '}';
    }
}
