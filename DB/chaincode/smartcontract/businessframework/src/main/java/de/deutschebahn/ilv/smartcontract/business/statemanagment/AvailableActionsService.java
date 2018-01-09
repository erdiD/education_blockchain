package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.ObjectState;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by AlbertLacambraBasil on 06.11.2017.
 */
public class AvailableActionsService {

    private static final Logger logger = Logger.getLogger(AvailableActionsService.class.getName());

    private final Set<FlowStep> flows;
    private final UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory;

    public AvailableActionsService(Set<FlowStep> flows, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {
        this.flows = flows;
        this.userRoleCheckerFactory = userRoleCheckerFactory;
    }

    public List<String> getAvailableActions(User user, BusinessObject businessObject) {

        UserRoleChecker userRoleChecker = userRoleCheckerFactory.getUserRoleChecker(user, businessObject);

        List<String> interactions = businessObject.getState()
                .getAllowedObjectStateInteractions()
                .stream()
                .filter(dsi -> user.getMarketRole().contains(dsi.getRole()))
                .map(dsi -> dsi.getStateInteraction().name())
                .collect(toList());

        List<String> transitionActions = flows
                .stream()
                .filter(flow -> user.getMarketRole().contains(flow.getRole()))
                .filter(flow -> flow.getCurrentState() == businessObject.getState())
                .filter(userRoleChecker::hasRequiredRoleToRunActionFlow)
                .map(FlowStep::getStateTransitionAction)
                .map(Enum::name)
                .collect(toList());

        interactions.addAll(transitionActions);

        logger.info("[getAvailableActions] Allowed actions. "
                + "BusinessObjectType=" + getClass().getSimpleName()
                + ", businessObjectId=" + businessObject.getId()
                + ", businessObject state=" + businessObject.getState()
                + ", actions=" + interactions);
        return interactions;
    }

    public Collection<MarketRoleName> getRolesWithAvailableActions(ObjectState state) {
        return flows.stream()
                .filter(fm -> fm.getCurrentState() == state)
                .map(FlowStep::getRole)
                .collect(toSet());
    }

    public boolean canPerformSomeAction(User user, BusinessObject object) {
        return !getAvailableActions(user, object).isEmpty();
    }
}
