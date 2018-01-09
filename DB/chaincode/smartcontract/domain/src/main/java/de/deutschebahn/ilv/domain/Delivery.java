package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.*;

public class Delivery implements BusinessObject {

    private String id;
    private ObjectState state;
    private Date deliveryDate;
    private Date startDate;
    private Set<String> psps;
    private BigDecimal budget;
    private PaymentType paymentType;
    private ContractType contractType;
    private Date dateCreated;
    private Date lastModified;
    private String projectId;
    private List<HistoryEntry> historyEntries = new ArrayList<>();
    private List<DeliveryEntry> deliveryEntries = new ArrayList<>();
    private List<String> availableActions = new ArrayList<>();

    public Delivery() {
        psps = new HashSet<>();
    }

    @Override
    public ObjectState getState() {
        return state;
    }

    @Override
    public void setState(ObjectState state) {
        this.state = state;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Collection<String> getPsps() {
        return new ArrayList<>(psps);
    }

    public List<DeliveryEntry> getDeliveryEntries() {
        return deliveryEntries;
    }

    public void setDeliveryEntries(List<DeliveryEntry> deliveryEntries) {
        this.deliveryEntries = deliveryEntries;
    }

    public void setPsps(Set<String> psps) {
        this.psps = psps;
    }

    public void setPsps(List<String> psps) {
        this.psps = new HashSet<>(psps);
    }

    public void addPsp(String psp) {
        psps.add(psp);
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    @Override
    public void updateFromObject(Persistable businessObject) {
        Delivery delivery = (Delivery) businessObject;
        setProjectId(delivery.getProjectId());
        setPsps(new HashSet<>(delivery.getPsps()));
        setBudget(delivery.getBudget());
        setContractType(delivery.getContractType());
        setPaymentType(delivery.getPaymentType());
        setStartDate(delivery.getStartDate());
        setLastModified(delivery.getLastModified());
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
    public String getOrganizationId() {
        return null;
    }

    @Override
    public void setOrganizationId(String ownerOrg) {

    }

    @Override
    public String getMessageBoardUrl() {
        return "";
    }

    @Override
    public void setMessageBoardUrl(String messageBoardUrl) {

    }

    @Override
    public void addAccessRole(MarketRoleName role) {

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
    public boolean hasRoleAccessed(MarketRoleName role) {
        return false;
    }

    @Override
    public Collection<MarketRoleName> getAccessedRoles() {
        return null;
    }
}
