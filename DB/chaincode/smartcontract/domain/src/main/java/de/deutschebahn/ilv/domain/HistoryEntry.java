package de.deutschebahn.ilv.domain;

import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 19.06.2017.
 */
public class HistoryEntry {

    private User user;
    private String objectId;
    private String projectId;
    private MarketRoleName marketRole;
    private ObjectState oldState;
    private ObjectState newState;
    private ObjectStateTransitionAction action;
    private Date creationTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public MarketRoleName getMarketRole() {
        return marketRole;
    }

    public void setMarketRole(MarketRoleName marketRole) {
        this.marketRole = marketRole;
    }

    public ObjectState getOldState() {
        return oldState;
    }

    public void setOldState(ObjectState oldState) {
        this.oldState = oldState;
    }

    public ObjectState getNewState() {
        return newState;
    }

    public void setNewState(ObjectState newState) {
        this.newState = newState;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public ObjectStateTransitionAction getAction() {
        return action;
    }

    public void setAction(ObjectStateTransitionAction action) {
        this.action = action;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
