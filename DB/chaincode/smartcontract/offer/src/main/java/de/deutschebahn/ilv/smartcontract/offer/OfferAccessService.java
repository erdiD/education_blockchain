package de.deutschebahn.ilv.smartcontract.offer;

import de.deutschebahn.ilv.domain.Offer;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.commons.OfferDataConverter;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class OfferAccessService extends ObjectAccessService<Offer> {
    private static final Logger logger = Logger.getLogger(OfferAccessService.class.getName());

    public OfferAccessService(AvailableActionsService availableActionsService, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {
        super(availableActionsService, chaincodeStub, userRoleCheckerFactory);
    }

    @Override
    protected boolean canReadImpl(User user, Offer offer) {
        //TODO: is demand creator, but its not taking care a change of roles
        if (user.getId().equalsIgnoreCase(offer.getCreatorId())) {
            return true;
        }

        boolean hasAvailableActions = getAvailableActionsService().canPerformSomeAction(user, offer);

        if (hasAvailableActions) {
            return true;
        }

        if (offer.getState().hasNotBeenPublishedOnMarket()) {
            return false;
        }

        boolean userHasAlreadyAccessed = RemoteCallClient.userHasAlreadyAccessed(chaincodeStub, user.getId(), offer.getProjectId());
        if (userHasAlreadyAccessed) {
            return true;
        }

        boolean userHasActionsOnContract = RemoteCallClient.canPerformActionOverProjectContract(chaincodeStub, user.getId(), offer.getProjectId());

        if (userHasActionsOnContract) {
            return true;
        }

        boolean userHasActionsOnDelivery = RemoteCallClient.canPerformActionOverProjectDelivery(chaincodeStub, user.getId(), offer.getProjectId());

        if (userHasActionsOnDelivery) {
            return true;
        }

        return false;
    }

    public boolean canPerformActionOnProjectOffers(User user, String projectId) {
        OfferFacade offerFacade = new OfferFacade(new OfferDataConverter(), chaincodeStub);
        return offerFacade
                .findAll(projectId)
                .stream()
                .filter(offer -> userCanPerformActionOverObject(user, offer))
                .peek(offer -> logger.info(String.format(
                        "[canPerformActionOnProjectOffers] User can perform action over offer # User=%s, offer=%s",
                        user.getId(), offer.getId()))
                ).count() > 0;
    }
}
