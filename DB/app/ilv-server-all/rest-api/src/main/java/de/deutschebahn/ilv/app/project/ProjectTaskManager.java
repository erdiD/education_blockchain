package de.deutschebahn.ilv.app.project;

import de.deutschebahn.ilv.bussinesobject.ContractFacade;
import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.Offer;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectTaskManager {

    @Inject
    Logger logger;

    @Inject
    ContractFacade contractFacade;


    public ProjectView createProjectTask(ProjectView projectView) {

        projectView.setProjectTask(this.getProjectTask(projectView));
        return projectView;
    }


    public ProjectTask getProjectTask(ProjectView projectView) {

        Demand demand = projectView.getDemand();
        List<Offer> offers = projectView.getOffers();

        switch (demand.getState()) {

            case NOT_CREATED:
            case DEMAND_OPENED:
                return ProjectTask.DEMAND_IN_PROGRESS;

            case DEMAND_BLOCKED:
            case DEMAND_CLOSED:
            case DEMAND_EXPIRED:
            case DEMAND_REJECTED:
                return ProjectTask.DEMAND_DENIED;

            case DEMAND_COMPLETED:
            case DEMAND_LOCKED:
            case DEMAND_SUBMITTED:

                if (offers.size() == 0) {
                    return ProjectTask.DEMAND_PUBLISHED;
                }

                ProjectTask mostAccomplishedOfferState = this.getMostAccomplishedOfferState(offers);
                logger.info("mostAccomplishedOfferState = " + mostAccomplishedOfferState);

                // OfferFlow completed, check the contract
                if (mostAccomplishedOfferState != null && mostAccomplishedOfferState == ProjectTask.OFFER_COMPLETED) {
                    Optional<Contract> optContractByDemand = contractFacade.getContractByProject(demand.getProjectId());

                    if (optContractByDemand.isPresent()) {
                        Contract contract = optContractByDemand.get();
                        ProjectTask mostAccomplishedContratState = this.getMostAccomplishedContractState(contract);
                        return mostAccomplishedContratState;
                    }
                }
                return mostAccomplishedOfferState;

            default:
                return ProjectTask.UNKNOWN;
        }

    }

    private ProjectTask getMostAccomplishedContractState(Contract contract) {

        ObjectState cState = contract.getState();

        switch (cState) {

            case CONTRACT_SIGNED:
                return ProjectTask.CONTRACT_SIGNED;

            case CONTRACT_REJECTED:
            case CONTRACT_TERMINATED:
                return ProjectTask.CONTRACT_DENIED;

            case CONTRACT_NOT_CREATED:
            case CONTRACT_CREATED:
            case CONTRACT_CLIENT_SIGNED:
            case CONTRACT_SUPPLIER_SIGNED:

                return ProjectTask.OFFER_COMPLETED;

            default:
                return ProjectTask.UNKNOWN;
        }

    }


    private ProjectTask getMostAccomplishedOfferState(List<Offer> offers) {

//			Set<ObjectState> stateList = offers.stream().map(Offer::getState).collect(Collectors.toSet());

        Set<ObjectState> stateList = offers.stream()
                .filter(o -> {
                    if (o.getState() == ObjectState.OFFER_CLOSED
                            || o.getState() == ObjectState.OFFER_EXPIRED
                            || o.getState() == ObjectState.OFFER_REJECTED) {
                        return false;
                    } else {
                        return true;
                    }
                })
                .map(Offer::getState)
                .collect(Collectors.toSet());

        if (stateList.size() == 0) {
            stateList.add(ObjectState.OFFER_NOT_CREATED);
        }

        logger.info("statelist: " + stateList);


        if (stateList.contains(ObjectState.OFFER_COMPLETED)
                || stateList.contains(ObjectState.OFFER_LOCKED)
                ) {

            return ProjectTask.OFFER_COMPLETED;
        }

        if (stateList.contains(ObjectState.OFFER_ACCEPTED)
                || stateList.contains(ObjectState.OFFER_TECH_APPROVED)
                || stateList.contains(ObjectState.OFFER_COMM_APPROVED)
                ) {

            return ProjectTask.OFFER_ACCEPTED;
        }

        if (stateList.contains(ObjectState.OFFER_OFFERED)
                || stateList.contains(ObjectState.OFFER_APPROVED) // just for the MAAS flow, not sure if it is right here
//					|| stateList.contains(ObjectState.OFFER_REVIEWED) // seems to be not used?
                ) {

            return ProjectTask.OFFER_OFFERED;
        }


        if (stateList.contains(ObjectState.OFFER_WAITING)
                ) {

            return ProjectTask.OFFER_WAITING_FOR_APPROVAL;
        }


        if (stateList.contains(ObjectState.OFFER_OPENED)
                ) {

            return ProjectTask.OFFER_IN_PROGRESS;
        }


        if (stateList.contains(ObjectState.OFFER_EXPIRED)
                || stateList.contains(ObjectState.OFFER_REJECTED)
                || stateList.contains(ObjectState.OFFER_CLOSED)

                ) {

            return ProjectTask.OFFER_DENIED;
        }

        if (stateList.contains(ObjectState.OFFER_NOT_CREATED)
                ) {

            return ProjectTask.DEMAND_PUBLISHED;
        }

        logger.info("[getMostAccomplishedOfferState] - Offerstate is unknown and not mapped to a ProjectState!");
        return ProjectTask.UNKNOWN;
    }


}
