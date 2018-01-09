package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.*;

public class Demand implements BusinessObject {

    private String id;
    private String projectId;
    private String organizationId;
    private String creatorId;
    private Date dateCreated;
    private Date lastModified;
    private String name;
    private Date endDate;
    private String targetAccount;
    private BigDecimal budget;
    private Priority priority;
    private String description;
    private String messageBoardUrl;
    private ObjectState state;
    private Set<MarketRoleName> accessedRoles;
    private List<HistoryEntry> historyEntries = new ArrayList<>();
    private List<AttachmentEntity> attachmentEntities = new ArrayList<>();
    private List<String> availableActions = new ArrayList<>();

    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<HistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    public Demand() {
        accessedRoles = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String ownerOrg) {
        this.organizationId = ownerOrg;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getMessageBoardUrl() {
        return messageBoardUrl;
    }

    @Override
    public void setMessageBoardUrl(String messageBoardUrl) {
        this.messageBoardUrl = messageBoardUrl;
    }

    @Override
    public void addAccessRole(MarketRoleName role) {
        accessedRoles.add(role);
    }

    @Override
    public boolean hasRoleAccessed(MarketRoleName role) {
        return accessedRoles.contains(role);
    }

    @Override
    public Collection<MarketRoleName> getAccessedRoles() {
        return new ArrayList<>(accessedRoles);
    }

    public void setAccessedRoles(Set<MarketRoleName> accessedRoles) {
        this.accessedRoles = accessedRoles;
    }

    @Override
    public List<AttachmentEntity> getAttachmentEntities() {
        return attachmentEntities;
    }

    @Override
    public void setAttachmentEntities(ArrayList<AttachmentEntity> attachmentEntities) {
        this.attachmentEntities = attachmentEntities;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ObjectState getState() {
        return state;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }

    public void setState(ObjectState state) {
        this.state = state;
    }

    public boolean isAvailableOnMarket() {
        return state.isAvailableOnMarket();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }


    public List<String> getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(List<String> availableActions) {
        this.availableActions = availableActions;
    }

    @Override
    public void updateFromObject(Persistable businessObject) {
        Demand updateDemand = (Demand) businessObject;
        setName(updateDemand.getName());
        setDescription(updateDemand.getDescription());
        setBudget(updateDemand.getBudget());
        setPriority(updateDemand.getPriority());
        setBudget(getBudget());
        setPriority(getPriority());
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Demand demand = (Demand) o;

        return id != null ? id.equals(demand.id) : demand.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Demand{" +
                "id=" + getId() +
                ", state=" + getState() +
                ", name='" + name + '\'' +
                ", accessedRoles" + getAccessedRoles() +
                ", creator=" + getCreatorId() +
                ", ownerOrg=" + getOrganizationId() +
                ", budget=" + budget +
                ", priority=" + priority +
                ", description='" + description + '\'' +
                ", messageBoardUrl='" + getMessageBoardUrl() + '\'' +
                '}';
    }
}
