package de.deutschebahn.ilv.app.demand;

import de.deutschebahn.ilv.app.ObjectHistoryConverter;
import de.deutschebahn.ilv.app.attachment.AttachmentDataConverter;
import de.deutschebahn.ilv.app.offer.OfferDataConverter;
import de.deutschebahn.ilv.app.organization.OrganizationDataConverter;
import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.bussinesobject.OrganizationProvider;
import de.deutschebahn.ilv.bussinesobject.UsersProvider;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.domain.Priority;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.getValueOrException;

/**
 * Created by alacambra on 04.06.17.
 */
public class DemandDataConverter {

    @Inject
    UserDataConverter userDataConverter;

    @Inject
    OfferDataConverter offerDataConverter;

    @Inject
    ObjectHistoryConverter objectHistoryConverter;

    @Inject
    OrganizationDataConverter organizationDataConverter;

    @Inject
    AttachmentDataConverter attachmentDataConverter;

    @Inject
    UsersProvider usersProvider;

    @Inject
    OrganizationProvider organizationProvider;

    public Demand deserialize(JsonObject demandJson) {
        Demand demand = new Demand();
        demand.setName(demandJson.getString("name", "no-name-given-or-req-error"));
        demand.setBudget(SerializationHelper.convertToBigDecimal(demandJson.getString("budget", "0")));
        demand.setDescription(demandJson.getString("description", ""));
        demand.setEndDate(getValueOrException("endDate", demandJson::getString, SerializationHelper::convertToDate));
        demand.setMessageBoardUrl(demandJson.getString("messageBoardUrl", ""));
        demand.setPriority(Priority.valueOf(demandJson.getString("priority", Priority.LOW.name()).toUpperCase()));
        demand.setTargetAccount(SerializationHelper.getValueOrException("targetAccount", demandJson::getString));
        return demand;
    }

    public JsonObject serialize(Demand demand, List<Offer> offers, Map<Object, Collection<String>> availableActions) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("id", demand.getId())
                .add("name", demand.getName())
                .add("state", SerializationHelper.removeObjectTypeFromObjectState(demand.getState()))
                .add("budget", SerializationHelper.serializeBigDecimalCurrency(demand.getBudget()))
                .add("priority", demand.getPriority().name())
                .add("description", demand.getDescription())
                .add("endDate", SerializationHelper.convertFromDate(demand.getEndDate()))
                .add("messageBoardUrl", demand.getMessageBoardUrl())
                .add("offers", offerDataConverter.serialize(offers, demand.getId(), availableActions))
                .add("creator", userDataConverter.serialize(usersProvider.getUser(demand.getCreatorId())))
                .add("ownerOrg", organizationDataConverter.serialize(organizationProvider.getOrganization(demand.getOrganizationId())))
                .add("creationTime", SerializationHelper.convertFromDate(demand.getDateCreated()))
                .add("availableActions", SerializationHelper.serializeToJsonArray(availableActions.get(demand)))
                .add("targetAccount", demand.getTargetAccount())
                .add("history", objectHistoryConverter.serialize(demand.getHistoryEntries()))
                .add("attachments", attachmentDataConverter.toJsonArray(demand.getAttachmentEntities()))
                ;

        return builder.build();
    }

    public JsonObject serialize(Demand demand, Map<Object, Collection<String>> availableActions) {
        return serialize(demand, Collections.emptyList(), availableActions);
    }
}