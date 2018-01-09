package de.deutschebahn.ilv.smartcontract.business.demand;

import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Arrays;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class DemandAccessService extends ObjectAccessService<Demand> {

    private static final List<ObjectState> demandFinishedStates;

    static {
        demandFinishedStates = Arrays.asList(
                ObjectState.DEMAND_COMPLETED,
                ObjectState.DEMAND_LOCKED,
                ObjectState.DEMAND_REJECTED,
                ObjectState.DEMAND_CLOSED
        );
    }

    public DemandAccessService(AvailableActionsService availableActionsService, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {
        super(availableActionsService, chaincodeStub, userRoleCheckerFactory);
    }

    @Override
    protected boolean canReadImpl(User user, Demand demand) {

        if (demand.getCreatorId().equals(user.getId())) {
            return true;
        }

        boolean hasAvailableActions = !getAvailableActionsService().getAvailableActions(user, demand).isEmpty();
        if (hasAvailableActions) {
            return true;
        }

        boolean userHasAlreadyAccessed = RemoteCallClient.userHasAlreadyAccessed(chaincodeStub, user.getId(), demand.getProjectId());
        if (userHasAlreadyAccessed) {
            return true;
        }

        if (demand.getState().hasNotBeenPublishedOnMarket()) {
            return false;
        }

        if (demandCanReceiveOffers(demand) && user.getMarketRole().contains(MarketRoleName.OFFER_CREATOR)) {
            return true;
        }

        boolean canPerformActionOnProjectOffer = RemoteCallClient.canPerformActionOverProjectOffer(chaincodeStub, user.getId(), demand.getProjectId());
        if (canPerformActionOnProjectOffer) {
            return true;
        }

        boolean canPerformActionOnProjectProject = RemoteCallClient.canPerformActionOverProjectContract(chaincodeStub, user.getId(), demand.getProjectId());
        if (canPerformActionOnProjectProject) {
            return true;
        }

        boolean canPerformActionOnProjectDelivery = RemoteCallClient.canPerformActionOverProjectDelivery(chaincodeStub, user.getId(), demand.getProjectId());
        if (canPerformActionOnProjectDelivery) {
            return true;
        }

        return false;
    }

    public boolean demandCanReceiveOffers(Demand demand) {
        return demand.getState().isAvailableOnMarket() && !demandFinishedStates.contains(demand.getState());
    }
}
