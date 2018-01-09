package de.deutschebahn.ilv.bussinesobject.entity;

import de.deutschebahn.ilv.domain.HistoryEntry;
import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = HistoryEntryEntity.getHistoryEntries, query = "SELECT h FROM HistoryEntryEntity as h where h.objectId LIKE :id")
public class HistoryEntryEntity {

    public static final String getHistoryEntries = "HistoryEntryEntity.getHistoryEntries";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    private MarketRoleName marketRole;
    private ObjectState oldState;
    private ObjectState newState;
    private ObjectStateTransitionAction action;
    private String txId;
    private String objectId;
    private String projectId;
    private Date creationTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoryEntryEntity fromHistoryEntry(HistoryEntry historyEntry) {
        marketRole = historyEntry.getMarketRole();
        oldState = historyEntry.getOldState();
        newState = historyEntry.getNewState();
        action = historyEntry.getAction();
        creationTime = historyEntry.getCreationTime();
        objectId = historyEntry.getObjectId();
        projectId = historyEntry.getProjectId();
        user = new UserEntity();
        user.fromUser(historyEntry.getUser());
        return this;
    }

    public HistoryEntry toDomainHistoryEntry() {
        HistoryEntry historyEntry = new HistoryEntry();
        historyEntry.setMarketRole(marketRole);
        historyEntry.setOldState(oldState);
        historyEntry.setNewState(newState);
        historyEntry.setAction(action);
        historyEntry.setCreationTime(creationTime);
        historyEntry.setObjectId(objectId);
        historyEntry.setProjectId(projectId);
        historyEntry.setUser(user.toDomainUser());
        return historyEntry;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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


    public Date getCreationTime() {
        return creationTime;
    }


    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }


    public String toString() {
        return "HistoryEntryEntity{" +
                "id=" + id +
                ", user=" + user +
                ", marketRole=" + marketRole +
                ", oldState=" + oldState +
                ", newState=" + newState +
                ", action=" + action +
                ", txId='" + txId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
