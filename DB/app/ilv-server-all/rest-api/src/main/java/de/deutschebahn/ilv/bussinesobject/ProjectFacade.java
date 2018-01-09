package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Demand;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by AlbertLacambraBasil on 28.08.2017.
 */
public class ProjectFacade {

    @PersistenceContext
    EntityManager em;

    //TODO: implement function in chaincode that updates las time modification
    public long getProjectLastModifiedTimestamp(Demand demand) {

//        //TODO: Set in the offer facade when exists
//        Date relatedOffersLastModificationDate = em
//                .createNamedQuery(Offer.maxLastModifiedByDemand, Date.class)
//                .setParameter("demand", demand)
//                .getSingleResult();
//
//        long relatedOffersTimestamps = 0;
//        if (relatedOffersLastModificationDate != null) {
//            relatedOffersTimestamps = relatedOffersLastModificationDate.getTime();
//        }
//
//        long demandsLastModifiedTimestampInMs = demand.getTimestampsEntity().getLastModified().getTime();
//        long overallLastModifiedTimeStamp = relatedOffersTimestamps >= demandsLastModifiedTimestampInMs ?
//                relatedOffersTimestamps : demandsLastModifiedTimestampInMs;
//
//        return overallLastModifiedTimeStamp;
        return 0;
    }

}
