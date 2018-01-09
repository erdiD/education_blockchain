package de.deutschebahn.ilv.domain;

/**
 * Created by AlbertLacambraBasil on 04.09.2017.
 */
public interface Persistable extends TimestampedEntity {
    String getId();

    void setId(String id);

    default void updateFromObject(Persistable businessObject) {
        throw new UnsupportedOperationException("updateFromObject not implemented for " + getClass());
    }

}
