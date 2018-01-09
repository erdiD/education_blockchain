package de.deutschebahn.ilv.app.demand;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.app.attachment.Attachment;
import de.deutschebahn.ilv.app.attachment.AttachmentDataConverter;
import de.deutschebahn.ilv.app.attachment.AttachmentStorageService;
import de.deutschebahn.ilv.bussinesobject.AttachmentEntityFacade;
import de.deutschebahn.ilv.bussinesobject.DemandFacade;
import de.deutschebahn.ilv.bussinesobject.ObjectHistoryService;
import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.commons.IdUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by alacambra on 02.06.17.
 */
@Path("demand")
@Stateful
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DemandResource {

    @PersistenceContext
    EntityManager em;

    @Inject
    DemandDataConverter dataConverter;

    @Inject
    DemandFacade demandFacade;

    @Inject
    ObjectHistoryService objectHistoryService;

    @Inject
    AttachmentStorageService attachmentStorageService;

    @Inject
    AttachmentDataConverter attachmentDataConverter;

    @Inject
    AttachmentEntityFacade attachmentEntityFacade;

    @Inject
    Logger logger;

    @Context
    HttpServletRequest request;

    @GET
    @Path("{demandId}")
    @Deprecated
    public Response getDemandById(@PathParam("demandId") String demandId) {

        Demand demand = demandFacade.getById(demandId).orElseThrow(() -> ClientException.createNotFoundError(demandId, Demand.class));
        //TODO: reenable it
//        Map<Object, Collection<String>> actionsMap = availableActionsFacade.getAvailableActionsAsMap(user, demand, demand.getOffers(), null, null);
//        List<Offer> offers = demand.getOffers()
//                .stream()
//                .filter(offer -> authorizationService.canRead(user, offer))
//                .collect(Collectors.toList());
//
//        JsonObject object = dataConverter.serialize(demand, offers, actionsMap);
//        logger.info("[getDemandById] converted demand is " + object);
//
//        return Response.ok(object).build();
        return Response.ok().build();
    }


    @POST
    public Response createDemand(JsonObject jsonDemand, @QueryParam("directSubmit") @DefaultValue("false") boolean directSubmit) throws URISyntaxException {

        Demand demand = dataConverter.deserialize(jsonDemand);
        demand.setState(ObjectState.NOT_CREATED);
        demand = demandFacade.merge(demand);

        if (directSubmit) {
            logger.info("[createDemand] directly publishing demand with id " + demand.getId());
            demand = demandFacade.fireAction(demand.getId(), ObjectStateTransitionAction.SUBMIT_DEMAND);
        }

        return Response.created(new URI("/")).entity(Json.createObjectBuilder().add("id", demand.getId()).build()).build();
    }

    @PUT
    @Path("{demandId}")
    public Response updateDemand(@PathParam("demandId") String demandId, JsonObject jsonDemand) {

        //Not sure what client will send, so regenerate it
        String did  = IdUtils.getDemandId(demandId);
        Demand updateDemand = dataConverter.deserialize(jsonDemand);
        Demand persistedDemand = demandFacade.getById(did).orElseThrow(() -> ClientException.createNotFoundError(did, Demand.class));
        persistedDemand.updateFromObject(updateDemand);
        updateDemand = demandFacade.merge(persistedDemand);
        logger.info("[updateDemand] updated demand is=" + updateDemand);
        return Response.noContent().build();
    }

    @PUT
    @Path("state/{demandId}")
    public Response updateDemandState(@PathParam("demandId") String demandId, @QueryParam("action") String action) {

        if (action == null) {
            throw ClientException.createParamNotGivenException("action");
        }

        demandFacade.fireAction(demandId, ObjectStateTransitionAction.valueOf(action));
        return Response.noContent().build();
    }

    @DELETE()
    @Path("{demandId}")
    public Response deleteDemand(@PathParam("demandId") int demandId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("{demandId}/attachment/{fileId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadAttachment(@PathParam("fileId") String fileId, @PathParam("demandId") String demandId) {
        //Not sure what client will send, so regenerate it
        String did = IdUtils.getDemandId(demandId);
        Demand demand = demandFacade.getById(did).orElseThrow(() -> ClientException.createNotFoundError(did, Demand.class));
        Attachment attachment = new Attachment(fileId);
        ByteArrayOutputStream outputStream = attachmentStorageService.getAttachment(attachment);

        return Response.ok(outputStream.toByteArray())
                .header("content-disposition", "attachment; filename = " + attachment.getFileName())
                .build();
    }

    @POST
    @Path("{demandId}/attachment")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAttachment(MultipartFormDataInput formDataInput, @PathParam("demandId") String demandId) {

        //Not sure what client will send, so regenerate it
        demandId = IdUtils.getDemandId(demandId);
        demandFacade.fireAction(demandId, ObjectStateTransitionAction.UPDATE);
        Attachment attachment = new Attachment(formDataInput, Offer.class, demandId);
        attachmentStorageService.storeAttachment(attachment);
        logger.info("[uploadAttachment] attachment stored" + attachment);

        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setOwnerObjectId(demandId);
        attachmentEntity.setHash(attachment.getFileHash());
        attachmentEntity.setFileName(attachment.getFileId());
        attachmentEntity.setFileSizeInBytes(attachment.getFileSizeInBytes());

        Demand demand = demandFacade.saveAttachment(attachmentEntity);
        logger.info("[uploadAttachment] attachmentEntity persisted with id=" + attachmentEntity.getId());

        return Response.ok(attachmentDataConverter.toJson(attachment)).build();
    }
}