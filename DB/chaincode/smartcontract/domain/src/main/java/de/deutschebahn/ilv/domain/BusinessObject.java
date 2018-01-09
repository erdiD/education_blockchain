package de.deutschebahn.ilv.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 09.06.2017.
 */
public interface BusinessObject extends TimestampedEntity, Persistable {

    String getOrganizationId();

    void setOrganizationId(String ownerOrg);

    String getMessageBoardUrl();

    void setMessageBoardUrl(String messageBoardUrl);

    ObjectState getState();

    void setState(ObjectState state);

    void addAccessRole(MarketRoleName role);

    boolean hasRoleAccessed(MarketRoleName role);

    Collection<MarketRoleName> getAccessedRoles();

    String getProjectId();

    void setProjectId(String projectId);

    List<AttachmentEntity> getAttachmentEntities();

    void setAttachmentEntities(ArrayList<AttachmentEntity> attachmentEntities);

    List<String> getAvailableActions();

    void setAvailableActions(List<String> availableActions);
}
