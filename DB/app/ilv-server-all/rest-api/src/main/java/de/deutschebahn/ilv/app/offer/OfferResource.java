package de.deutschebahn.ilv.app.offer;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.app.attachment.Attachment;
import de.deutschebahn.ilv.app.attachment.AttachmentDataConverter;
import de.deutschebahn.ilv.app.attachment.AttachmentStorageService;
import de.deutschebahn.ilv.bussinesobject.AttachmentEntityFacade;
import de.deutschebahn.ilv.bussinesobject.DemandFacade;
import de.deutschebahn.ilv.bussinesobject.ObjectHistoryService;
import de.deutschebahn.ilv.bussinesobject.OfferFacade;
import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.commons.IdUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by alacambra on 02.06.17.
 */
@Path("offer")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfferResource {

    @Inject
    Logger logger;

    @Inject
    DemandFacade demandFacade;

    @Inject
    OfferFacade offerFacade;

    @Inject
    AttachmentEntityFacade attachmentEntityFacade;

    @Inject
    ObjectHistoryService objectHistoryService;

    @Inject
    AttachmentStorageService attachmentStorageService;

    @Inject
    AttachmentDataConverter attachmentDataConverter;

    @Inject
    OfferDataConverter dataConverter;

    @POST
    public Response createOffer(JsonObject jsonOffer) throws URISyntaxException {

        logger.info("receiving new offer=" + jsonOffer);

        if (!jsonOffer.containsKey("demandId")) {
            throw ClientException.createParamNotGivenException("demandId");
        }

        String projectId = IdUtils.extractProjectId(jsonOffer.getString("demandId"));
        Offer offer = dataConverter.deserialize(jsonOffer);
        offer.setProjectId(projectId);
        checkIfDatesAreValid(offer);

        offer = offerFacade.merge(offer);
        return Response.created(new URI("/"))
                .entity(Json.createObjectBuilder()
                        .add("id", offer.getId())
                        .build()
                ).build();
    }


    private void checkIfDatesAreValid(Offer offer) {

        long startDate = offer.getStartDate().getTime();
        String demandId = IdUtils.getDemandId(offer.getProjectId());
        Demand demand = demandFacade.getById(demandId)
                .orElseThrow(() -> ClientException.createNotFoundError(demandId, Demand.class));

        long endDate = demand.getEndDate().getTime();
        long demandDeliveryDate = offer.getDeliveryDate().getTime();

        if (startDate > endDate)
            throw ClientException.createClientException("The Startdate has to be before the Enddate");
        if (startDate > demandDeliveryDate)
            throw ClientException.createClientException("The Startdate has to be before the Deliverydate");
        if (demandDeliveryDate > endDate)
            throw ClientException.createClientException("The Enddate has to be before the Deliverydate");
    }

    @PUT
    @Path("{offerId}")
    public Response updateOffer(@PathParam("offerId") String offerId, JsonObject jsonOffer) {

        Offer updateOffer = dataConverter.deserialize(jsonOffer);

        Offer persistedOffer = offerFacade.getById(offerId)
                .orElseThrow(() -> ClientException.createNotFoundError(offerId, Demand.class));

        persistedOffer.updateFromObject(updateOffer);

        checkIfDatesAreValid(persistedOffer);
        updateOffer = offerFacade.merge(persistedOffer);
        logger.info("[updateOffer] offer updated # offer=" + updateOffer);

        return Response.noContent().build();
    }

    @PUT
    @Path("state/{id}")
    public Response updateOfferState(@PathParam("id") String offerId, @QueryParam("action") String action) {

        if (action == null) {
            throw ClientException.createParamNotGivenException("action");
        }
        offerFacade.fireAction(offerId, ObjectStateTransitionAction.valueOf(action));
        return Response.noContent().build();
    }

    @POST
    @Path("{offerId}/attachment")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAttachment(MultipartFormDataInput formDataInput, @PathParam("offerId") String offerId) {

        offerFacade.fireAction(offerId, ObjectStateTransitionAction.UPDATE);
        Attachment attachment = new Attachment(formDataInput, Offer.class, offerId);
        attachmentStorageService.storeAttachment(attachment);
        logger.info("[uploadAttachment] attachment stored" + attachment);

        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setOwnerObjectId(offerId);
        attachmentEntity.setHash(attachment.getFileHash());
        attachmentEntity.setFileName(attachment.getFileId());
        attachmentEntity.setFileSizeInBytes(attachment.getFileSizeInBytes());

        Offer offer = offerFacade.saveAttachment(attachmentEntity);
        logger.info("[uploadAttachment] attachmentEntity persisted with id=" + attachmentEntity.getId());

        return Response.ok(attachmentDataConverter.toJson(attachment)).build();
    }

    @GET
    @Path("{offerId}/attachment/{fileId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadAttachment(@PathParam("fileId") String fileId, @PathParam("offerId") String offerId) {

        Offer offer = offerFacade.getById(offerId).orElseThrow(() -> ClientException.createNotFoundError(offerId, Offer.class));
        Attachment attachment = new Attachment(fileId);
        ByteArrayOutputStream outputStream = attachmentStorageService.getAttachment(attachment);

        return Response.ok(outputStream.toByteArray())
                .header("content-disposition", "attachment; filename = " + attachment.getFileName())
                .build();
    }

}
