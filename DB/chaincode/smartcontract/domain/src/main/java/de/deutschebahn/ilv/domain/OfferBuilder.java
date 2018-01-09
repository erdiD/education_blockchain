package de.deutschebahn.ilv.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 10.10.2017.
 */
public final class OfferBuilder implements CanBuild<Offer> {
    private String id;
    private String creatorId;
    private PaymentType paymentType;
    private BigDecimal price;
    private Date dateCreated;
    private Date lastModified;
    private Date deliveryDate;
    private Date startDate;
    private ContractType contractType;
    private String description;
    private String descriptionDocumentURL;
    private ObjectState state = ObjectState.NO_STATE;
    private String messageBoardUrl;
    private String projectId;
    private String organizationId;

    private OfferBuilder() {
    }

    public static OfferBuilder anOffer() {
        return new OfferBuilder();
    }

    public OfferBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public OfferBuilder withCreator(String creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public OfferBuilder withPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public OfferBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OfferBuilder withDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public OfferBuilder withLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public OfferBuilder withDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public OfferBuilder withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public OfferBuilder withContractType(ContractType contractType) {
        this.contractType = contractType;
        return this;
    }

    public OfferBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public OfferBuilder withDescriptionDocumentURL(String descriptionDocumentURL) {
        this.descriptionDocumentURL = descriptionDocumentURL;
        return this;
    }

    public OfferBuilder withState(ObjectState state) {
        this.state = state;
        return this;
    }

    public OfferBuilder withMessageBoardUrl(String messageBoardUrl) {
        this.messageBoardUrl = messageBoardUrl;
        return this;
    }

    public OfferBuilder withProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public OfferBuilder withOrganizationId(String orgId) {
        this.organizationId = orgId;
        return this;
    }

    public Offer build() {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setCreatorId(creatorId);
        offer.setPaymentType(paymentType);
        offer.setPrice(price);
        offer.setDateCreated(dateCreated);
        offer.setLastModified(lastModified);
        offer.setDeliveryDate(deliveryDate);
        offer.setStartDate(startDate);
        offer.setContractType(contractType);
        offer.setDescription(description);
        offer.setDescriptionDocumentURL(descriptionDocumentURL);
        offer.setState(state);
        offer.setMessageBoardUrl(messageBoardUrl);
        offer.setProjectId(projectId);
        offer.setOrganizationId(organizationId);
        return offer;
    }
}
