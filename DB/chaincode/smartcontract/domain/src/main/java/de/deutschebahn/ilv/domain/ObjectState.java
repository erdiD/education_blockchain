package de.deutschebahn.ilv.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.deutschebahn.ilv.domain.MarketRelation.*;

/**
 * States on which a Demand object can be. For each of this states, different interactions can be fired with the demand.
 * Which actions are available depends of the state as well as the role of the user triggering the action.
 * Interactions does not produces any state change on the demand.
 */
public enum ObjectState {

    NO_STATE(0),

    NOT_CREATED(hasNotBeenPublished),
    DEMAND_OPENED(hasNotBeenPublished),
    DEMAND_SUBMITTED(isAvailableOnMarket,
            new RoleInteractionPair<>(ObjectStateInteraction.MAKE_OFFER, MarketRoleName.OFFER_CREATOR)
    ),
    DEMAND_ACCEPTED(isAvailableOnMarket,
            new RoleInteractionPair<>(ObjectStateInteraction.MAKE_OFFER, MarketRoleName.OFFER_CREATOR)
    ),
    DEMAND_BLOCKED(isAvailableOnMarket,
            new RoleInteractionPair<>(ObjectStateInteraction.MAKE_OFFER, MarketRoleName.OFFER_CREATOR)
    ),
    DEMAND_EXPIRED(hasBeenPublished),
    DEMAND_REJECTED(hasBeenPublished),
    DEMAND_COMPLETED(hasBeenPublished),
    DEMAND_CLOSED(hasBeenPublished),
    DEMAND_LOCKED(hasBeenPublished),

    OFFER_NOT_CREATED(hasNotBeenPublished),
    OFFER_OPENED(hasNotBeenPublished),
    OFFER_WAITING(hasNotBeenPublished),
    OFFER_APPROVED(hasNotBeenPublished),
    OFFER_OFFERED(isAvailableOnMarket),
    OFFER_ACCEPTED(isAvailableOnMarket),
    OFFER_EXPIRED(hasBeenPublished),
    OFFER_COMPLETED(hasBeenPublished),
    OFFER_REJECTED(hasBeenPublished),
    OFFER_REVIEWED(hasBeenPublished),
    OFFER_TECH_APPROVED(hasBeenPublished),
    OFFER_COMM_APPROVED(hasBeenPublished),
    OFFER_CLOSED(hasBeenPublished),
    OFFER_LOCKED(hasBeenPublished),
    CONTRACT_NOT_CREATED,

    CONTRACT_CREATED,
    CONTRACT_SUPPLIER_SIGNED,
    CONTRACT_CLIENT_SIGNED,
    CONTRACT_SIGNED,
    CONTRACT_REJECTED,
    CONTRACT_TERMINATED,

    DELIVERY_NOT_CREATED,
    DELIVERY_CREATED,
    DELIVERY_ACTIVE(new RoleInteractionPair<>(ObjectStateInteraction.UPLOAD_DELIVERY, MarketRoleName.SUPPLIER_PROJECT_MANAGER)),
    DELIVERY_COMPLETE,
    DELIVERY_CLOSED;


    private List<RoleInteractionPair<ObjectStateInteraction>> allowedDemandStateInteractions;
    private int marketRelation;

    /**
     * State of a demand
     *
     * @param availableOnMarket:     if true, this state implies that the BO is available on market
     * @param demandStateInteraction
     */
    ObjectState(int availableOnMarket, RoleInteractionPair<ObjectStateInteraction>... demandStateInteraction) {
        this.marketRelation = availableOnMarket;
        this.allowedDemandStateInteractions = Arrays.asList(demandStateInteraction);
    }

    ObjectState(RoleInteractionPair<ObjectStateInteraction>... demandStateInteraction) {
        this.marketRelation = hasNotBeenPublished;
        this.allowedDemandStateInteractions = Arrays.asList(demandStateInteraction);
    }

    public List<RoleInteractionPair<ObjectStateInteraction>> getAllowedObjectStateInteractions() {
        return allowedDemandStateInteractions;
    }

    public boolean isAvailableOnMarket() {
        return marketRelation == isAvailableOnMarket;
    }

    public boolean hasBeenPublishedOnMarket() {
        return marketRelation == hasBeenPublished;
    }

    public boolean hasNotBeenPublishedOnMarket() {
        return marketRelation == hasNotBeenPublished;
    }

    public boolean isInteractionAllowed(RoleInteractionPair roleInteractionPair) {
        return getAllowedObjectStateInteractions().contains(roleInteractionPair);
    }

    public boolean isInteractionAllowed(ObjectStateInteraction demandStateInteraction, Collection<MarketRole> roles) {
        return roles.stream()
                .map(role -> new RoleInteractionPair<ObjectStateInteraction>(demandStateInteraction, role.getRoleName()))
                .anyMatch(this::isInteractionAllowed);
    }
}

class MarketRelation {
    public final static int hasNotBeenPublished = 0;
    public final static int isAvailableOnMarket = 1;
    public final static int hasBeenPublished = 2;
}
