package de.deutschebahn.ilv.app.project;

import de.deutschebahn.ilv.app.ClientException;
import de.deutschebahn.ilv.app.contract.ContractDataConverter;
import de.deutschebahn.ilv.app.delivery.DeliveryDataConverter;
import de.deutschebahn.ilv.app.demand.DemandDataConverter;
import de.deutschebahn.ilv.app.offer.OfferDataConverter;
import de.deutschebahn.ilv.app.organization.OrganizationDataConverter;
import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by AlbertLacambraBasil on 11.08.2017.
 */
public class ProjectDataConverter {

    @Inject
    DemandDataConverter demandDataConverter;

    @Inject
    OfferDataConverter offerDataConverter;

    @Inject
    ContractDataConverter contractDataConverter;

    @Inject
    OrganizationDataConverter organizationDataConverter;

    @Inject
    DeliveryDataConverter deliveryDataConverter;

    @Inject
    UserDataConverter userDataConverter;

    public JsonObject toProjectView(Demand demand,
                                    List<Offer> offers,
                                    Contract contract,
                                    Delivery delivery,
                                    Map<Object, Collection<String>> availableActions,
                                    ProjectTask projectTask) {


        JsonArray jsonOffers = offerDataConverter.serialize(offers, demand.getId(), availableActions);
        JsonObject demandJson = demandDataConverter.serialize(demand, availableActions);

        JsonObject contractJson;
        if (contract != null) {
            Offer selectedOffer = offers.stream()
                    .filter(off -> off.getId().equalsIgnoreCase(contract.getOfferId()))
                    .findAny()
                    .orElseThrow(() -> ClientException.createNotFoundError(contract.getOfferId(), Offer.class));
            //TODO: upgrade converters to support this view, or create new views
            Collection<String> actions = availableActions.get(contract);
            contractJson = contractDataConverter.serialize(contract, demand, selectedOffer, actions);
        } else {
            contractJson = Json.createObjectBuilder().build();
        }

        JsonObject jsonDelivery;
        if (delivery != null) {

            jsonDelivery = deliveryDataConverter.serialize(delivery, availableActions.get(delivery));
        } else {
            jsonDelivery = Json.createObjectBuilder().build();
        }

        return Json.createObjectBuilder()
                .add("contract", contractJson)
                .add("demand", demandJson)
                .add("offers", jsonOffers)
                .add("delivery", jsonDelivery)
                .add("task", projectTask.name())
                .build();
    }

    public JsonObject toProjectListView(List<ProjectView> projectViews) {

        JsonArray projectsViewArr = projectViews.stream()
                .map(this::toJson)
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();

        return Json.createObjectBuilder().add("projects", projectsViewArr).build();
    }

    private JsonObject toJson(ProjectView projectView) {
        Demand demand = projectView.getDemand();
        return toJson(demand.getId(),
                demand.getName(),
                demand.getState(),
                projectView.getProjectTask(),
                //TODO: correctly pass objects
                Json.createObjectBuilder().build(),//organizationDataConverter.serialize(demand.getOwnerOrg()),
                Json.createObjectBuilder().build(),//userDataConverter.serialize(demand.getCreator()),
                demand.getBudget(),
                projectView.getOverallLastModifiedTimeStamp());
    }

    private JsonObject toJson(String id, String name, ObjectState demandState, ProjectTask task,
                              JsonObject ownerOrganization, JsonObject creator, BigDecimal budget,
                              Long overallLastModifiedTimeStamp) {

        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("task", task.name())
                .add("state", SerializationHelper.removeObjectTypeFromObjectState(demandState))
                .add("budget", SerializationHelper.serializeBigDecimalCurrency(budget))
                .add("ownerOrganization", ownerOrganization)
                .add("creator", creator)
                .add("lastModified", overallLastModifiedTimeStamp)
                .build();
    }
}
