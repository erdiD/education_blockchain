package de.deutschebahn.ilv.app.delivery;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.app.attachment.Attachment;
import de.deutschebahn.ilv.app.attachment.AttachmentStorageService;
import de.deutschebahn.ilv.app.user.LoggedUser;
import de.deutschebahn.ilv.bussinesobject.AttachmentEntityFacade;
import de.deutschebahn.ilv.bussinesobject.ContractFacade;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryEntryFacade;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryFacade;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryParserResult;
import de.deutschebahn.ilv.domain.AttachmentEntity;
import de.deutschebahn.ilv.domain.Delivery;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by AlbertLacambraBasil on 10.08.2017.
 */
@Path("delivery/{deliveryId}/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateful
public class DeliveryResource {

    @Inject
    Logger logger;

    @Context
    UriInfo uriInfo;

    @Inject
    AttachmentStorageService attachmentStorageService;

    @Inject
    DeliveryFacade deliveryFacade;

    @Inject
    AttachmentEntityFacade attachmentEntityFacade;

    @Inject
    DeliveryEntryFacade deliveryEntryFacade;

    @Inject
    DeliveryDataConverter deliveryDataConverter;

    @Inject
    LoggedUser loggedUser;

    @Inject
    ContractFacade contractFacade;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDeliveryFile(MultipartFormDataInput formDataInput) {
        Delivery delivery = getDeliveryOrException();
        Attachment attachment = new Attachment(formDataInput, Delivery.class, delivery.getId().replace("\u0000", "#"));
        attachmentStorageService.storeAttachment(attachment);
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setFileName(attachment.getFileId());
        attachmentEntity.setHash(attachment.getFileHash());
        attachmentEntity.setOwnerObjectId(delivery.getId());

        DeliveryParserResult deliveryParserResult = deliveryFacade.processDelivery(
                delivery,
                attachment.getFileData(),
                attachmentEntity,
                loggedUser.getUser());

        logger.info("deliveryParserResult = " + deliveryParserResult);
        JsonObject jsonObject = deliveryDataConverter.serialize(delivery, deliveryParserResult);
        logger.info("final jsonObject = " + jsonObject);
        return Response.ok().entity(jsonObject).build();
    }

    @PUT
    public Response addPsp(@QueryParam("psps") @DefaultValue("") String psps) {

        Delivery delivery = getDeliveryOrException();

        //TODO: action should be add psp, but its not implemented yet
        if (psps.isEmpty()) {
            throw ClientException.createParamNotGivenException("psps");
        }

        String[] psp = psps.split(",");
        Stream.of(psp).forEach(delivery::addPsp);

        //TODO: finish implementation of addPsp. then disable above fireAction
        deliveryFacade.merge(delivery);
        return Response.ok().build();
    }

    private Delivery getDeliveryOrException() {
        String id = uriInfo.getPathParameters().get("deliveryId").get(0);
        Delivery delivery = deliveryFacade
                .getById(id)
                .orElseThrow(() -> ClientException.createNotFoundError(id, Delivery.class));

        return delivery;
    }
}
