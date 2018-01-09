package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.bussinesobject.entity.HistoryEntryEntity;
import de.deutschebahn.ilv.domain.HistoryEntry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 05.07.2017.
 */
@Stateless
public class ObjectHistoryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    Logger logger;

    public List<HistoryEntry> getHistoryEntries(String objectId) {
        List<HistoryEntryEntity> entries = em
                .createNamedQuery(HistoryEntryEntity.getHistoryEntries, HistoryEntryEntity.class)
                .setParameter("id", objectId)
                .getResultList();

        return entries.stream().map(HistoryEntryEntity::toDomainHistoryEntry).collect(Collectors.toList());
    }
}
