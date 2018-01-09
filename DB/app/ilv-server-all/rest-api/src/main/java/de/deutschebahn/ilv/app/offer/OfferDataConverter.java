package de.deutschebahn.ilv.app.offer;

import de.deutschebahn.ilv.app.ObjectHistoryConverter;
import de.deutschebahn.ilv.app.attachment.AttachmentDataConverter;
import de.deutschebahn.ilv.app.organization.OrganizationDataConverter;
import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.bussinesobject.DemandFacade;
import de.deutschebahn.ilv.bussinesobject.OrganizationProvider;
import de.deutschebahn.ilv.bussinesobject.UsersProvider;
import de.deutschebahn.ilv.domain.ContractType;
import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.domain.PaymentType;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;

import javax.inject.Inject;
import javax.json.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.deutschebahn.ilv.smartcontract.commons.SerializationHelper.*;

public class OfferDataConverter {

    @Inject
    UserDataConverter userDataConverter;

    @Inject
    ObjectHistoryConverter objectHistoryConverter;

    @Inject
    OrganizationDataConverter organizationDataConverter;

    @Inject
    AttachmentDataConverter attachmentDataConverter;

    @Inject
    DemandFacade demandFacade;

    @Inject
    UsersProvider usersProvider;

    @Inject
    OrganizationProvider organizationProvider;

    public Offer deserialize(JsonObject jsonOffer) {

        getValueOrException("deliveryDate", jsonOffer::getString, SerializationHelper::convertToDate);

        Offer offer = new Offer();
        offer.setDescription(jsonOffer.getString("description"));
        offer.setPrice(convertToBigDecimal(jsonOffer.getString("price", "0")));
        offer.setMessageBoardUrl(jsonOffer.getString("messageBoardUrl", ""));
        offer.setPaymentType(getValueOrException("paymentType", jsonOffer::getString, PaymentType::valueOf));
        offer.setDeliveryDate(getValueOrException("deliveryDate", jsonOffer::getString, SerializationHelper::convertToDate));
        offer.setStartDate(convertToDate(jsonOffer.getString("startDate")));
        offer.setContractType(getValueOrException("contractType", jsonOffer::getString, ContractType::valueOf));
        return offer;
    }

    public JsonObject serialize(Offer offer, String demandId, Collection<String> availableActions) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("demandId", demandId)
                .add("id", offer.getId())
                .add("price", serializeBigDecimalCurrency(offer.getPrice()))
                .add("description", offer.getDescription())
                .add("messageBoardUrl", offer.getMessageBoardUrl())
                .add("deliveryDate", convertFromDate(offer.getDeliveryDate()))
                .add("startDate", convertFromDate(offer.getStartDate()))
                .add("state", removeObjectTypeFromObjectState(offer.getState()))
                .add("paymentType", offer.getPaymentType().name())
                .add("contractType", offer.getContractType().name())
                .add("availableActions", serializeToJsonArray(availableActions))
                .add("creationDate", convertFromDate(offer.getDateCreated()))
                .add("creator", userDataConverter.serialize(usersProvider.getUser(offer.getCreatorId())))
                .add("ownerOrg", organizationDataConverter.serialize(organizationProvider.getOrganization(offer.getOrganizationId())))
                .add("history", objectHistoryConverter.serialize(offer.getHistoryEntries()))
                .add("attachments", attachmentDataConverter.toJsonArray(offer.getAttachmentEntities()))
                ;

        return builder.build();
    }

    public JsonArray serialize(List<Offer> offers, String demandId, Map<Object, Collection<String>> availableActions) {

        if (offers == null || offers.isEmpty()) {
            return Json.createArrayBuilder().build();
        }

        return offers
                .stream()
//                //in case offer for demand exist but is not available to the user. Actually on serialization the list of offer should be passed
//                .filter(availableActions::containsKey)
                .map(offer -> serialize(
                        offer,
                        demandId, availableActions.get(offer))
                ).collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();
    }
}
