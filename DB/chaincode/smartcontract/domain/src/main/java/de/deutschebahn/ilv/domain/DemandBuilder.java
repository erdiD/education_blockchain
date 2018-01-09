package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 10.10.2017.
 */
public final class DemandBuilder implements CanBuild<Demand> {
    private String id;
    private Date dateCreated;
    private Date lastModified;
    private String name;
    private Date endDate;
    private String creatorId;
    private User creator;
    private String targetAccount;
    private String organizationId;
    private BigDecimal budget;
    private Priority priority;
    private String description;
    private String messageBoardUrl;
    private ObjectState state;
    private String projectId;
    private String userId;

    private DemandBuilder() {
    }

    public static DemandBuilder aDemand() {
        return new DemandBuilder();
    }

    public DemandBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public DemandBuilder withDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public DemandBuilder withLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public DemandBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DemandBuilder withEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public DemandBuilder withCreatorId(String creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public DemandBuilder withCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public DemandBuilder withTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
        return this;
    }

    public DemandBuilder withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public DemandBuilder withBudget(BigDecimal budget) {
        this.budget = budget;
        return this;
    }

    public DemandBuilder withPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public DemandBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public DemandBuilder withMessageBoardUrl(String messageBoardUrl) {
        this.messageBoardUrl = messageBoardUrl;
        return this;
    }

    public DemandBuilder withState(ObjectState state) {
        this.state = state;
        return this;
    }

    public DemandBuilder withProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public DemandBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Demand build() {
        Demand demand = new Demand();
        demand.setId(id);
        demand.setProjectId(projectId);
        demand.setDateCreated(dateCreated);
        demand.setLastModified(lastModified);
        demand.setName(name);
        demand.setEndDate(endDate);
        demand.setCreatorId(creatorId);
        demand.setTargetAccount(targetAccount);
        demand.setOrganizationId(organizationId);
        demand.setBudget(budget);
        demand.setPriority(priority);
        demand.setDescription(description);
        demand.setMessageBoardUrl(messageBoardUrl);
        demand.setState(state);
        return demand;
    }
}
