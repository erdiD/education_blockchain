package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.*;

public class Offer implements BusinessObject {

    private String id;
    private String creatorId;
    private String organization;
    private PaymentType paymentType;
    private BigDecimal price;
    private BigDecimal demandBudget;
    private Date dateCreated;
    private Date lastModified;
    /**
     * Date when the contract (case that is signed) expires
     */
    private Date deliveryDate;
    private Date startDate;
    private ContractType contractType;
    private String description;
    private String descriptionDocumentURL;
    private ObjectState state = ObjectState.NO_STATE;
    private String messageBoardUrl;
    private Set<MarketRoleName> accessedRoles;
    private String projectId;
    private List<HistoryEntry> historyEntries = new ArrayList<>();
    private List<AttachmentEntity> attachmentEntities = new ArrayList<>();
    private List<String> availableActions = new ArrayList<>();

    public Offer() {
        description = "";
        messageBoardUrl = "";
        descriptionDocumentURL = "";
        accessedRoles = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getOrganizationId() {
        return organization;
    }

    public void setOrganizationId(String organization) {
        this.organization = organization;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDemandBudget() {
        return demandBudget;
    }

    public void setDemandBudget(BigDecimal demandBudget) {
        this.demandBudget = demandBudget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionDocumentURL() {
        return descriptionDocumentURL;
    }

    public void setDescriptionDocumentURL(String descriptionDocumentURL) {
        this.descriptionDocumentURL = descriptionDocumentURL;
    }

    public List<AttachmentEntity> getAttachmentEntities() {
        return attachmentEntities;
    }

    @Override
    public void setAttachmentEntities(ArrayList<AttachmentEntity> attachmentEntities) {
        this.attachmentEntities = attachmentEntities;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ObjectState getState() {
        return state;
    }

    public void setState(ObjectState state) {
        this.state = state;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public List<String> getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(List<String> availableActions) {
        this.availableActions = availableActions;
    }

    @Override
    public void updateFromObject(Persistable businessObject) {

        Offer updateOffer = (Offer) businessObject;

        setPrice(updateOffer.getPrice());
        setDescription(updateOffer.getDescription());
        setMessageBoardUrl(getMessageBoardUrl());
        setDescriptionDocumentURL(updateOffer.getDescriptionDocumentURL());
        setContractType(updateOffer.getContractType());
        setDeliveryDate(updateOffer.getDeliveryDate());
        setStartDate(updateOffer.getStartDate());
        setPaymentType(updateOffer.getPaymentType());
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getMessageBoardUrl() {
        return messageBoardUrl;
    }

    public void setMessageBoardUrl(String messageBoardUrl) {
        this.messageBoardUrl = messageBoardUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        return id != null ? id.equals(offer.id) : offer.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(List<HistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
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

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", organization=" + organization +
                ", paymentType=" + paymentType +
                ", price=" + price +
                ", deliveryDate=" + deliveryDate +
                ", startDate=" + startDate +
                ", contractType=" + contractType +
                ", description='" + description + '\'' +
                ", descriptionDocumentURL='" + descriptionDocumentURL + '\'' +
                ", state=" + state +
                ", messageBoardUrl='" + messageBoardUrl + '\'' +
                ", accessedRoles=" + accessedRoles +
                '}';
    }
}