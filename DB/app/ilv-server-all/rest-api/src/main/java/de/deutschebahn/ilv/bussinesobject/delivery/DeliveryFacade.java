package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.bussinesobject.BusinessObjectFacade;
import de.deutschebahn.ilv.bussinesobject.DateHelper;
import de.deutschebahn.ilv.bussinesobject.DemandFacade;
import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.client.BusinessObjectClient;
import de.deutschebahn.ilv.smartcontract.client.CommunicationResult;
import de.deutschebahn.ilv.smartcontract.client.delivery.DeliveryChaincodeClient;
import de.deutschebahn.ilv.smartcontract.commons.AttachmentDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DeliveryEntryDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.IdUtils;
import de.deutschebahn.ilv.smartcontract.commons.model.DeliveryEntrySet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 14.08.2017.
 */
public class DeliveryFacade extends BusinessObjectFacade<Delivery> {

    @Inject
    Logger logger;

    @Inject
    DeliveryTimerService deliveryTimerService;

    @Inject
    DeliveryEntryParser deliveryEntryParser;

    @Inject
    DemandFacade demandFacade;

    @Inject
    DeliveryChaincodeClient deliveryChaincodeClient;

    @PostConstruct
    public void init() {

    }

    @Override
    protected DeliveryChaincodeClient getChaincodeClient() {
        return deliveryChaincodeClient;
    }

    @Override
    public Optional<Delivery> getById(String id) {
        return checkCommunicationResultAndReturn(deliveryChaincodeClient.getById(id));
    }

    @Override
    public Delivery merge(Delivery delivery) {
        delivery = super.merge(delivery);
        deliveryTimerService.setDeliveryTimers(delivery);
        return delivery;
    }

    @Deprecated
    public Optional<Delivery> getDeliveryByDemand(Demand demand) {
        return checkCommunicationResultAndReturn(deliveryChaincodeClient.getById(demand.getProjectId()));
    }

    public Optional<Delivery> getDeliveryByProjectId(String projectId) {
        String deliveryId = IdUtils.getDeliveryId(projectId);
        return checkCommunicationResultAndReturn(deliveryChaincodeClient.getById(deliveryId));
    }

    public DeliveryParserResult processDelivery(Delivery delivery, InputStream attachment, AttachmentEntity attachmentEntity, User deliverer) throws NoPspForDeliveryFoundException {

        Objects.requireNonNull(delivery);
        Objects.requireNonNull(attachment);

        if (delivery.getPsps().isEmpty()) {
            throw NoPspForDeliveryFoundException.noPspsFound(delivery);
        }

        logger.info("[processDelivery]  delivery.getContractType()=" + delivery.getContractType());

        boolean isWorkAndServiceContract = delivery.getContractType() == ContractType.WORK_AND_SERVICE_CONTRACT;

        DeliveryParserResult parsingResults = deliveryEntryParser.parse(delivery.getPsps(), attachment, isWorkAndServiceContract);
        DeliveryParserResult deliveryParserResult = new DeliveryParserResult(parsingResults.getErrors());

        for (DeliveryEntry deliveryEntry : parsingResults.getDeliveryEntries()) {
            String error = checkForErrors(deliveryEntry, delivery);

            if (error.isEmpty()) {

                deliveryEntry.setDeliveryId(delivery.getId());
                deliveryEntry.setAttachmentEntityId(attachmentEntity.getId());
                deliveryEntry.setUserDelivererId(deliverer.getId());
                deliveryEntry.setCreationDate(new Date());
                deliveryEntry.setLastModified(new Date());
                deliveryParserResult.addDeliveryEntry(deliveryEntry);


            } else {
                deliveryParserResult.addError(new DeliveryEntryError(deliveryEntry.getLine(), error));
            }
        }

        if (!parsingResults.getDeliveryEntries().isEmpty()) {
            DeliveryEntrySet deliveryEntrySet = saveProcessedDelivery(delivery.getProjectId(), attachmentEntity, deliveryParserResult.getDeliveryEntries());
            deliveryParserResult.setPersistedDeliveryEntries(deliveryEntrySet.getDeliveryEntries());
            deliveryParserResult.setAttachmentEntity(deliveryEntrySet.getAttachmentEntity());
        } else {
            logger.info("[processDelivery] No deliveries found. Not Saving file. # deliveryParserResult=" + deliveryParserResult);
        }

        return deliveryParserResult;
    }

    private DeliveryEntrySet saveProcessedDelivery(String projectId, AttachmentEntity attachmentEntity, List<DeliveryEntry> deliveryEntries) {
        attachmentEntity.setDateCreated(new Date());
        attachmentEntity.setLastModified(attachmentEntity.getDateCreated());
        DeliveryEntrySet deliveryEntrySet =
                new DeliveryEntrySet(attachmentEntity
                        , deliveryEntries
                        //TODO: inject converters correctly
                        , new AttachmentDataConverter()
                        , new DeliveryEntryDataConverter()
                        , DataConverter.SerializeView.createJsonForNewObject
                );

        CommunicationResult<DeliveryEntrySet> communicationResult = deliveryChaincodeClient.saveProcessedDelivery(projectId, deliveryEntrySet);
        checkCommunicationResult(communicationResult);
        return communicationResult.getResult();
    }

    private String checkForErrors(DeliveryEntry deliveryEntry, Delivery delivery) {

        LocalDate deliveryStartDate = DateHelper.toLocalDate(delivery.getStartDate());
        LocalDate deliveryDeliveryDate = DateHelper.toLocalDate(delivery.getDeliveryDate());

        LocalDate deliveryEntryDate = DateHelper.toLocalDate(deliveryEntry.getDeliveryDate());
        LocalDate today = LocalDate.now();

        if (deliveryEntryDate.isAfter(today)) {
            return "The given date beStrings to the future";
        } else if (deliveryEntryDate.isBefore(deliveryStartDate)) {
            return "The given date is before delivery has began";
        } else if (deliveryEntryDate.isAfter(deliveryDeliveryDate)) {
            return "The given date is after delivery has ended";
        }

        return "";
    }

    @Override
    protected BusinessObjectClient<Delivery> getBusinessObjectClient() {
        return deliveryChaincodeClient;
    }
}
