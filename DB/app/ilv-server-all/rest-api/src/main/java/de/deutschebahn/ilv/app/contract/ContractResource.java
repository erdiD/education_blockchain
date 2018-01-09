package de.deutschebahn.ilv.app.contract;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.bussinesobject.ContractFacade;
import de.deutschebahn.ilv.bussinesobject.DemandFacade;
import de.deutschebahn.ilv.bussinesobject.OfferFacade;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 28.07.2017.
 */
@Path("contract")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateful
public class ContractResource {

    @Inject
    Logger logger;

    @Inject
    ContractFacade contractFacade;

    @Inject
    ContractDataConverter contractDataConverter;

    @Inject
    DemandFacade demandFacade;

    @Inject
    OfferFacade offerFacade;

    @GET
    @Path("offer/{offerId}")
    public Response getContractOfOffer(@PathParam("offerId") String contractId) {
        return Response.ok().build();
    }

    @PUT
    @Path("state/{contractId}")
    public Response updateContractState(@PathParam("contractId") String contractId, @QueryParam("action") String action) {
        if (action == null) {
            throw ClientException.createParamNotGivenException("action");
        }
        contractFacade.fireAction(contractId, ObjectStateTransitionAction.valueOf(action));
        return Response.noContent().build();
    }
}