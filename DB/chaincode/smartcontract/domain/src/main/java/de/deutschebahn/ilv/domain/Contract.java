package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Contract implements BusinessObject {

    private static Logger logger = Logger.getLogger(Contract.class.getName());
    private String id;
    private String projectId;
    private String offerId;
    private ObjectState contractState;
    private Date deliveryDate;
    private Date startDateDate;
    private BigDecimal budget;
    private ContractType contractType;
    private PaymentType paymentType;
    private Date dateCreated;
    private Date lastModified;
    private List<HistoryEntry> historyEntries;
    private List<String> availableActions = new ArrayList<>();


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getOrganizationId() {
        //TODO: is that correct? should belongs to 2 orgs? Is implicit enough
        throw new UnsupportedOperationException("Contract does nto belong to a unique org");
    }

    @Override
    public void setOrganizationId(String ownerOrg) {
        //TODO: is that correct? should belongs to 2 orgs? Is implicit enough
        throw new UnsupportedOperationException("Contract does nto belong to a unique org");
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Override
    public String getMessageBoardUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMessageBoardUrl(String messageBoardUrl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectState getState() {
        return this.contractState;
    }

    @Override
    public void setState(ObjectState state) {
        this.contractState = state;
    }

    @Override
    public void addAccessRole(MarketRoleName role) {

    }

    public ObjectState getContractState() {
        return contractState;
    }

    public void setContractState(ObjectState contractState) {
        this.contractState = contractState;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getStartDate() {
        return startDateDate;
    }

    public void setStartDateDate(Date startDateDate) {
        this.startDateDate = startDateDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public void updateFromObject(Persistable businessObject) {
        Contract contract = (Contract) businessObject;
        setProjectId(contract.getProjectId());
        setState(contract.getState());
        setLastModified(contract.getLastModified());
        setDateCreated(contract.getDateCreated());
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public List<AttachmentEntity> getAttachmentEntities() {
        return new ArrayList<>();
    }

    @Override
    public void setAttachmentEntities(ArrayList<AttachmentEntity> attachmentEntities) {

    }

    @Override
    public boolean hasRoleAccessed(MarketRoleName role) {
        return false;
    }

    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<HistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    public List<String> getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(List<String> availableActions) {
        this.availableActions = availableActions;
    }

    @Override
    public Collection<MarketRoleName> getAccessedRoles() {
        return null;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                "state=" + contractState +
                '}';
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
