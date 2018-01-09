package de.deutschebahn.ilv.app.project;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.app.PerformanceCheck;
import de.deutschebahn.ilv.app.PerformanceInterceptor;
import de.deutschebahn.ilv.app.user.LoggedUser;
import de.deutschebahn.ilv.bussinesobject.*;
import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryFacade;
import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.commons.IdUtils;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by alacambra on 02.06.17.
 */
@Path("project")
@Produces(MediaType.APPLICATION_JSON)
@Interceptors(PerformanceInterceptor.class)
public class ProjectResource {

    @Inject
    Logger logger;

    @Inject
    DemandFacade demandFacade;

    @Inject
    ContractFacade contractFacade;

    @Inject
    OfferFacade offerFacade;

    @Inject
    ProjectFacade projectFacade;

    @Inject
    DeliveryFacade deliveryFacade;

    @Inject
    ProjectDataConverter projectDataConverter;

    @Inject
    ObjectHistoryService objectHistoryService;

    @Inject
    ProjectTaskManager projectTaskManager;

    @Inject
    LoggedUser loggedUser;

    @GET
    public Response getProjects() {

        List<Demand> demands = demandFacade.findAll();

        List<ProjectView> projectViews = demands
                .stream()
                .map(this::fromDemandToProjectView)
                .map(projectTaskManager::createProjectTask)
                .collect(Collectors.toList());

        JsonObject jsonObject = projectDataConverter.toProjectListView(projectViews);
        return Response.ok(jsonObject).build();
    }

    private ProjectView fromDemandToProjectView(Demand demand) {
        //TODO: reenable it
//        Long overallLastModifiedTimeStamp = projectFacade.getProjectLastModifiedTimestamp(demand);
        Long overallLastModifiedTimeStamp = 0L;
        List<Offer> offers = Collections.emptyList();
        return new ProjectView(demand, offers, overallLastModifiedTimeStamp);
    }

    @GET
    @Path("{projectId}")
    @PerformanceCheck
    @Asynchronous
    public void getProject(@PathParam("projectId") String pid, final @Suspended AsyncResponse response) throws ExecutionException, InterruptedException {

        logger.info("[getProject] User executor is = " + loggedUser);
        CompletableFuture.supplyAsync(() -> {
            try {
                return getProject(pid);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            if (throwable.getCause() instanceof ClientException) {
                ClientException clientException = (ClientException) throwable.getCause();
                response.resume(clientException);
                return null;
            } else {
                response.resume(throwable);
                return null;
            }
        }).thenAccept(response::resume);

    }


    JsonObject getProject(String pid) throws ExecutionException, InterruptedException {
        //Never know which id comes....
        String demandId = IdUtils.getDemandId(pid);
        String projectId = IdUtils.extractProjectId(pid);
        try {
            CompletableFuture<Optional<Demand>> demandCf = CompletableFuture
                    .supplyAsync(() -> demandFacade.getById(demandId))
                    .thenApply(demand -> {
                        demand.orElseThrow(() -> ClientException.createNotFoundError(demandId, Demand.class));
                        demand.ifPresent(dem -> {
                            dem.setHistoryEntries(objectHistoryService.getHistoryEntries(projectId));
                        });
                        return demand;
                    });

            CompletableFuture<List<Offer>> offersCf = CompletableFuture
                    .supplyAsync(() -> {
                        List<Offer> offers = tryToReachObject(() -> offerFacade.getOffersOfProject(projectId));
                        offers.stream().forEach(off -> off.setHistoryEntries(objectHistoryService.getHistoryEntries(off.getId())));
                        return offers;
                    });


            CompletableFuture<Contract> contractCf = CompletableFuture
                    .supplyAsync(() -> {
                        Contract contract = tryToReachObject(() -> contractFacade
                                .getContractByProject(IdUtils.extractProjectId(projectId))
                                .orElse(null));

                        return contract;
                    });

            CompletableFuture<Delivery> deliveryCf = CompletableFuture
                    .supplyAsync(() -> {
                        Delivery delivery = tryToReachObject(() -> deliveryFacade.getDeliveryByProjectId(projectId)
                                .orElse(null));
                        return delivery;
                    });

            CompletableFuture<Void> f = CompletableFuture.allOf(demandCf, offersCf, contractCf, deliveryCf);
            f.get();
            //TODO: analyze roles
//        List<Offer> offers = demand.getOffers()
//                .stream()
//                .filter(offer -> authorizationService.canRead(user, offer)
//                        || 		user.getMarketRole().stream()
//                        .map(MarketRole::getRoleName)
//                        .anyMatch(r -> r == MarketRoleName.SUPPLIER_PROJECT_MANAGER))
//                .collect(Collectors.toList());

            Demand demand = demandCf.get().get();
            List<Offer> offers = offersCf.get();
            Contract contract = contractCf.get();
            Delivery delivery = deliveryCf.get();

            //TODO: remove it when serializers updated
            Map<Object, Collection<String>> availableActions = new HashMap<>();
            availableActions.put(demand, demand.getAvailableActions());
            offers.forEach(off -> availableActions.put(off, off.getAvailableActions()));

            if (contract != null) {
                availableActions.put(contract, contract.getAvailableActions());
                contract.setHistoryEntries(objectHistoryService.getHistoryEntries(contract.getId()));
            }

            if (delivery != null) {
                delivery.setHistoryEntries(objectHistoryService.getHistoryEntries(delivery.getId()));
                availableActions.put(delivery, delivery.getAvailableActions());
            }

//            Map<Object, Collection<String>> availableActions = availableActionsFacade.getAvailableActionsAsMap(
//                    demand,
//                    offers,
//                    contract,
//                    delivery);


            ProjectView projectView = this.fromDemandToProjectView(demand);
            ProjectTask projectTask = projectTaskManager.getProjectTask(projectView);

            JsonObject jsonObject = projectDataConverter.toProjectView(demand, offers, contract, delivery, availableActions, projectTask);

            return jsonObject;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ClientException) {
                ClientException clientException = (ClientException) e.getCause();
                throw clientException;
            }
            throw e;
        }
    }

    private <T> T tryToReachObject(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (ClientException e) {
            logger.info("[tryToReachObject] Not possible to reachObject. # e=" + e);
        }

        return null;
    }
}
