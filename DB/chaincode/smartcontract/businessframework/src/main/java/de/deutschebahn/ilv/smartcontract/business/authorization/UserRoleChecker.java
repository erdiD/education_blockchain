package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.FlowStep;

import java.util.List;
import java.util.logging.Logger;

public class UserRoleChecker implements RoleChecker {

    private static final Logger LOG = Logger.getLogger(UserRoleChecker.class.getName());
    private User user;
    private BusinessObject businessObject;

    public UserRoleChecker(User user, BusinessObject businessObject) {
        this.user = user;
        this.businessObject = businessObject;
    }

    @Override
    public boolean hasRequiredRoleToRunActionFlow(FlowStep flowStep) {

        List<MarketRoleName> roles = user.getMarketRole();

        boolean result = roles
                .stream()
                .filter(this::roleAppliesForUser)
                .anyMatch(flowStep.getRole()::equals);

        LOG.info("[hasRequiredRoleToRunActionFlow] user has role:" + result
                + ", required role=" + flowStep.getRole()
                + ", found roles=" + roles
                + ", flowStep=" + flowStep
                + ", user=" + user.getUserName() + ", businessObject=" + businessObject.getId());

        return result;
    }

    /**
     * In some cases, the role should be check vs the user, e.g. demandCreator
     *
     * @param marketRoleName
     * @return
     */
    protected boolean roleAppliesForUser(MarketRoleName marketRoleName) {

        if (businessObject instanceof Demand) {
            Demand demand = (Demand) businessObject;
            boolean hasDemandCreatorRole = marketRoleName == MarketRoleName.DEMAND_CREATOR;
            boolean isTheCreatorOfTheDemand = demand.getCreatorId().equals(user.getId());

            /**
             * In case is demand creator, should also be creator of the demand
             */
            return !hasDemandCreatorRole || isTheCreatorOfTheDemand;
        } else if (businessObject instanceof Offer) {
            Offer offer = (Offer) businessObject;
            boolean isOfferCreator = marketRoleName == MarketRoleName.OFFER_CREATOR;
            boolean isTheCreatorOfTheOffer = offer.getCreatorId().equals(user.getId());

            return !isOfferCreator || isTheCreatorOfTheOffer;
        }

        return true;
    }

    @Override
    public String toString() {
        return "UserRoleChecker{" +
                "user=" + user.getUserName() +
                ", businessObject=" + businessObject.getId() +
                ", businessObjectType=" + businessObject.getClass().getSimpleName() +
                '}';
    }

    public interface UserRoleCheckerFactory {
        UserRoleChecker getUserRoleChecker(User u, BusinessObject businessObject);
    }
}
