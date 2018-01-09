package de.deutschebahn.ilv.app.stats;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.bussinesobject.StatsFacade;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryFacade;
import de.deutschebahn.ilv.domain.ContractType;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.Demand;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 18.08.2017.
 */
@Path("stats")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class StatsResource {

    @Inject
    DeliveryFacade deliveryFacade;

    @Inject
    StatsFacade statsFacade;

    @Inject
    StatsDataConverter statsDataConverter;

    @Context
    HttpServletRequest request;

    @Inject
    Logger logger;

    @GET
    @Path("{demandId}")
    public JsonObject getStatsForProject(@PathParam("demandId") String demandId) {

        Delivery delivery = deliveryFacade.getDeliveryByProjectId(demandId)
                .orElseThrow(() -> ClientException.createNotFoundError(demandId, Demand.class));

		if (delivery.getContractType() == ContractType.SERVICE_CONTRACT) {

			ServiceDeliveryView serviceDeliveryView = new ServiceDeliveryView.ServiceDeliveryViewBuilder()
					.setProjectDuration(statsFacade.getProjectDuration(delivery).getDays())
					.setBudget(delivery.getBudget())
					.setTotalPaidInPercent(statsFacade.getTotalPaidUntilNowInPercent(delivery))
					.setTotalUsedTimeInPercent(statsFacade.getTotalUsedTimeInPercent(delivery))
					.setProgressInPercent(statsFacade.getTotalUsedTimeInPercent(delivery))
					.setPerformedPaymentPerMonth(statsFacade.getWeeklyUsedBudget(delivery))
					.setContractType(delivery.getContractType())
					.createServiceDeliveryView();

			return statsDataConverter.serialize(serviceDeliveryView);
		} else if (delivery.getContractType() == ContractType.WORK_AND_SERVICE_CONTRACT) {

			WorkAndServiceDeliveryView workAndServiceDeliveryView = new WorkAndServiceDeliveryView.WorkAndServiceDeliveryViewBuilder()
					.setAchievedScope(statsFacade.getAccomplishedScopeInPercent(delivery))
					.setProgressInPercent(statsFacade.getTotalProgressUntilNowInPercent(delivery))
					.setBudget(delivery.getBudget())
					.setAchievedScopePerMonth(statsFacade.getMonthlyAccomplishedScope(delivery))
					.setPerformedPaymentPerMonth(statsFacade.getWeeklyUsedBudget(delivery))
					.setProjectDuration(statsFacade.getProjectDuration(delivery).getDays())
					.setTotalPaidInPercent(statsFacade.getTotalPaidUntilNowInPercent(delivery))
					.setContractType(delivery.getContractType())
					.createWorkAndServiceDeliveryView();

			return statsDataConverter.serialize(workAndServiceDeliveryView);
		} else if (delivery.getContractType() == ContractType.SUBSCRIPTION_CONTRACT) {

			delivery = StatsFacade.createWeeklyDeliveryEntries(delivery);

			DeliveryView deliveryView = new DeliveryView.DeliveryViewBuilder().setBudget(delivery.getBudget())
					.setPerformedPaymentPerMonth(statsFacade.getWeeklyUsedBudget(delivery))
					.setProjectDuration(statsFacade.getProjectDurationInDays(delivery))
					.setTotalPaidInPercent(statsFacade.getTotalPaidUntilNowInPercent(delivery))
					.setContractType(delivery.getContractType())
					.setProgressInPercent(statsFacade.getTotalPaidUntilNowInPercent(delivery)).createDeliveryView();

			return statsDataConverter.serialize(deliveryView);
		}
		return Json.createObjectBuilder().build();
	}





}
