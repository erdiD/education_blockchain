package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.smartcontract.business.InvalidActionException;
import de.deutschebahn.ilv.smartcontract.commons.ChaincodeResponseMessage;
import de.deutschebahn.ilv.smartcontract.commons.ClientException;
import de.deutschebahn.ilv.smartcontract.commons.ErrorPayload;
import de.deutschebahn.ilv.smartcontract.commons.MessageStatus;
import org.hyperledger.fabric.shim.Chaincode;

import javax.json.JsonObject;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 29.08.2017.
 */
public class ExceptionMapper {
    private static final Logger logger = Logger.getLogger(ExceptionMapper.class.getName());

    public ExceptionMapper() {
    }

    public Chaincode.Response handleExceptionAndGetResponse(Exception e) {

        String message = e.getClass().getName() + ":" + e.getMessage();
        JsonObject resp = new ErrorPayload(message, getCode(e)).toJson();
        ChaincodeResponseMessage messageResponse = new ChaincodeResponseMessage(getCode(e), resp);
        logger.info("[handleExceptionAndGetResponse] Sending error response=" + resp);
        return new Chaincode.Response(getServerStatus(messageResponse), message, messageResponse.asBytes());
    }

    private Chaincode.Response.Status getServerStatus(ChaincodeResponseMessage messageResponse) {
        return messageResponse.getStatus() == MessageStatus.INTERNAL_ERROR ?
                Chaincode.Response.Status.INTERNAL_SERVER_ERROR : Chaincode.Response.Status.SUCCESS;
    }

    private MessageStatus getCode(Exception e) {
        if (e instanceof ClientException) {
            ClientException clientException = (ClientException) e;
            return clientException.getStatus();
        }else if(e instanceof InvalidActionException){
            return MessageStatus.ACTION_NOT_ACCEPTED;
        }

        return MessageStatus.INTERNAL_ERROR;
    }
}
