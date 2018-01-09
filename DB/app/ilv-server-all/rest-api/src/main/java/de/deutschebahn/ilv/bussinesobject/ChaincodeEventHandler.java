package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.bussinesobject.entity.HistoryEntryEntity;
import de.deutschebahn.ilv.bussinesobject.entity.UserEntity;
import de.deutschebahn.ilv.domain.HistoryEntry;
import de.deutschebahn.ilv.smartcontract.commons.ActionPerformedEvent;
import de.deutschebahn.ilv.smartcontract.commons.IdUtils;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;
import org.hyperledger.fabric.sdk.ChaincodeEvent;

import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 20.10.2017.
 */
@Stateless
public class ChaincodeEventHandler {

    private static final Logger logger = Logger.getLogger(ChaincodeEventHandler.class.getName());

    @PersistenceContext
    EntityManager em;

    public void handle(String txId, ChaincodeEvent chaincodeEvent) {
        try {
            JsonObject payload = SerializationHelper.bytesToJsonObject(chaincodeEvent.getPayload());
            ActionPerformedEvent actionPerformedEvent = new ActionPerformedEvent(payload.getJsonObject("payload"));
            HistoryEntry historyEntry = actionPerformedEvent.getHistoryEntry();
            HistoryEntryEntity historyEntryEntity = new HistoryEntryEntity().fromHistoryEntry(historyEntry);
            historyEntryEntity.setTxId(txId);
            UserEntity user = new UserEntity().fromUser(historyEntry.getUser());
            logger.info("[handle] Saving user=" + user);
            user = em.merge(user);
            logger.info("[handle] User saved=" + user);

            historyEntry.setUser(user.toDomainUser());

            historyEntryEntity.setObjectId(historyEntryEntity.getObjectId().replace(IdUtils.HL_SEPARATOR, IdUtils.EXT_SEPARATOR));
            historyEntryEntity.setProjectId(historyEntryEntity.getProjectId().replace(IdUtils.HL_SEPARATOR, IdUtils.EXT_SEPARATOR));
            historyEntryEntity = em.merge(historyEntryEntity);
            logger.info("[handle] Saved history entry. # HistoryEntry=" + historyEntryEntity);
        } catch (Exception e) {
            logger.warning("Error saving history entry. # Error=" + e.getMessage() + ", payload=" + new String(chaincodeEvent.getPayload()));
        }
    }
}
