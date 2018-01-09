package de.deutschebahn.ilv.domain;

import java.util.Date;

/**
 * Created by alacambra on 03.06.17.
 */
public interface TimestampedEntity {

    Date getDateCreated();

    void setDateCreated(Date dateCreated);

    Date getLastModified();

    void setLastModified(Date lastModified);
}
