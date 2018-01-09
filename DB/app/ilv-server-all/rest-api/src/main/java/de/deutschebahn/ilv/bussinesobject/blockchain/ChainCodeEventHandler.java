package de.deutschebahn.ilv.bussinesobject.blockchain;

import de.deutschebahn.ilv.bussinesobject.ChaincodeEventHandler;
import de.deutschebahn.ilv.smartcontract.client.SmartContractClient;
import de.deutschebahn.ilv.smartcontract.commons.ActionPerformedEvent;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by AlbertLacambraBasil on 20.10.2017.
 */
@Singleton
@Startup
public class ChainCodeEventHandler {

    @Inject
    SmartContractClient smartContractClient;

    @Inject
    ChaincodeEventHandler chaincodeEventHandler;

    private Channel channel;
    private String handlerId;
    private Timer timer;

    private static final Logger logger = Logger.getLogger(ChainCodeEventHandler.class.getName());

    public ChainCodeEventHandler() {
        logger.info("[ChainCodeEventHandler] Starting timer");
    }

    @PostConstruct
    public void init() {
        timer = new Timer();
        logger.info("[init] initializing registerChaincodeEventListener");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tryToConnect();
            }
        }, 0, 1000);
    }

    @PreDestroy
    public void stop() {
        try {
            if (chaincodeEventHandler != null)
                channel.unRegisterChaincodeEventListener(handlerId);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToConnect() {
        logger.info("[tryToConnect] Try to connect ");
        smartContractClient.tryInitChannel();
        channel = smartContractClient.getChannel();
        if (channel != null) {
            try {

                handlerId = channel.registerChaincodeEventListener(
                        Pattern.compile(".*"),
                        Pattern.compile(Pattern.quote(ActionPerformedEvent.NAME)),
                        createChaincodeEventListener()
                );

                timer.cancel();
                logger.info("[tryToConnect] SmartContractClient correctly connected");

            } catch (InvalidArgumentException e) {
                logger.warning("[tryToConnect] Error=" + e.getMessage());
            }
        } else {
            timer.cancel();
        }
    }

    private ChaincodeEventListener createChaincodeEventListener() {
        return (handle, blockEvent, chaincodeEvent) -> {
            logger.info("[createChaincodeEventListener] " + String.format(
                    "Received Chaincode event # handle=%s, chaincodeId=%s, chaincodeEventName=%s, transactionId=%s, eventPayload:=%s",
                    handle,
                    chaincodeEvent.getChaincodeId(),
                    chaincodeEvent.getEventName(),
                    chaincodeEvent.getTxId(),
                    new String(chaincodeEvent.getPayload()))
            );

            chaincodeEventHandler.handle(chaincodeEvent.getTxId(), chaincodeEvent);
        };
    }
}
