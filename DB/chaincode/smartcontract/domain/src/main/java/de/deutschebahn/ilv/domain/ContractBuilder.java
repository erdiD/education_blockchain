package de.deutschebahn.ilv.domain;

import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 14.10.2017.
 */
public final class ContractBuilder implements CanBuild<Contract> {
    private String id;
    private String projectId;
    private String offerId;
    private Date dateCreated;
    private Date lastModified;
    private String organizationId;

    private ContractBuilder() {
    }

    public static ContractBuilder aContract() {
        return new ContractBuilder();
    }

    public ContractBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ContractBuilder withProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public ContractBuilder withOfferId(String offerId) {
        this.offerId = offerId;
        return this;
    }

    public ContractBuilder withDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public ContractBuilder withLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public ContractBuilder withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Contract build() {
        Contract contract = new Contract();
        contract.setId(id);
        contract.setProjectId(projectId);
        contract.setOfferId(offerId);
        contract.setDateCreated(dateCreated);
        contract.setLastModified(lastModified);
        contract.setOrganizationId(organizationId);
        return contract;
    }
}
