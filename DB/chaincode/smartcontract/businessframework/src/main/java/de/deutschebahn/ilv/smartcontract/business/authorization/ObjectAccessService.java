package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public abstract class ObjectAccessService<T extends BusinessObject> {

    protected ChaincodeStub chaincodeStub;
    private final UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory;
    private AvailableActionsService availableActionsService;

    public ObjectAccessService(AvailableActionsService availableActionsService, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {
        this.availableActionsService = availableActionsService;
        this.chaincodeStub = chaincodeStub;
        this.userRoleCheckerFactory = userRoleCheckerFactory;
    }

    //NOTE:
    //No DemandCreator can see a not offered offer
    //No OfferCreator can see a not open demand
    //TODO: return AccessRequestResult. It should include the "forbidden" messages
    public final boolean canRead(User user, T object) {

        //If have some other role, it could see the object using this role
        if (user.getMarketRole().contains(MarketRoleName.DEMAND_CREATOR)) {
            String creatorId = (String) RemoteCallClient
                    .getProjectField(chaincodeStub, user.getId(), object.getProjectId(), "creatorId")
                    .orElseThrow(() -> new RuntimeException("creatorId not found # projectId=" + object.getProjectId()));

            //DemandCreator is NOT the creator of the project, this role does not apply.
            if (!creatorId.equals(user.getId())) {
                user.getMarketRole().remove(MarketRoleName.DEMAND_CREATOR);
                return false;
            }
        }

        return canReadImpl(user, object);
    }

    protected abstract boolean canReadImpl(User user, T object);

    protected boolean userCanPerformActionOverObject(User user, T object) {

        UserRoleChecker userRoleChecker = userRoleCheckerFactory.getUserRoleChecker(user, object);

        Set<MarketRoleName> roleNames = new HashSet<>(user.getMarketRole());
        Collection<MarketRoleName> availableRoles = availableActionsService
                .getRolesWithAvailableActions(object.getState())
                .stream()
                .filter(userRoleChecker::roleAppliesForUser)
                .collect(Collectors.toList());

        int max = availableRoles.size() + roleNames.size();
        roleNames.addAll(availableRoles);

        //If some role intersects on the set final set, it will be add only once, so if a user has a required role, the final set's
        //size will be smaller than the possible max size's set.
        if (max > roleNames.size()) {
            return true;
        }

        return object
                .getState()
                .getAllowedObjectStateInteractions()
                .stream()
                .anyMatch(dsi -> roleNames.contains(dsi.getRole()));
    }

    public AvailableActionsService getAvailableActionsService() {
        return availableActionsService;
    }

    public interface ObjectAccessServiceFactory<T extends BusinessObject> {
        ObjectAccessService<T> getObjectAccessService(ChaincodeStub chaincodeStub);
    }

}
