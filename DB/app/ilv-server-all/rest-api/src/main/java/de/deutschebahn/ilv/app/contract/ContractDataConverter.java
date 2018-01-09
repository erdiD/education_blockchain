package de.deutschebahn.ilv.app.contract;

import de.deutschebahn.ilv.app.ObjectHistoryConverter;
import de.deutschebahn.ilv.app.demand.DemandDataConverter;
import de.deutschebahn.ilv.app.offer.OfferDataConverter;
import de.deutschebahn.ilv.app.organization.OrganizationDataConverter;
import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alacambra on 04.06.17.
 */
public class ContractDataConverter {

    @Inject
    OfferDataConverter offerDataConverter;

    @Inject
    ObjectHistoryConverter objectHistoryConverter;

    @Inject
    DemandDataConverter demandDataConverter;

    @Inject
    OrganizationDataConverter organizationDataConverter;

    public JsonObject serialize(Contract contract, Demand demand, Offer offer, Collection<String> availableActions) {

        Map<Object, Collection<String>> emptyAvailableActions = new ConcurrentHashMap<>();
        emptyAvailableActions.put(demand, Collections.emptySet());
        emptyAvailableActions.put(offer, Collections.emptySet());

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("id", contract.getId())
                .add("state", SerializationHelper.removeObjectTypeFromObjectState(contract.getState()))
                .add("demand", demandDataConverter.serialize(demand, Collections.emptyList(), emptyAvailableActions))
                .add("offer", offerDataConverter.serialize(offer, demand.getId(), Collections.emptyList()))
                //TODO: activate history
                .add("history", objectHistoryConverter.serialize(contract.getHistoryEntries()) )
                .add("availableActions", SerializationHelper.serializeToJsonArray(availableActions));

        return builder.build();
    }
}
