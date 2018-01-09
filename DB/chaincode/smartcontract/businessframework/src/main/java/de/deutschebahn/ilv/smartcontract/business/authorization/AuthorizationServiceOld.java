package de.deutschebahn.ilv.smartcontract.business.authorization;

/**
 * Created by AlbertLacambraBasil on 07.08.2017.
 */
public class AuthorizationServiceOld {

//    private static final Logger logger = Logger.getLogger(AuthorizationServiceOld.class.getName());
//
//    private StateManager<Demand> demandStateManager;
//    private StateManager<Offer> offerStateManager;
//    private ChaincodeStub chaincodeStub;
//
//    public AuthorizationServiceOld() {
////        demandStateManager = new StateManager<>();
//    }
//
//
//    public boolean canRead(User user, Offer offer) {
//
//        Demand demand = RemoteCallClient
//                .getDemand(chaincodeStub, offer.getProjectId())
//                .orElseThrow(() -> ClientException.notFoundError(IdUtils.recreateDemandId(offer.getProjectId()), Demand.class));
//
//        Set<MarketRoleName> roleNames = user.getMarketRole()
//                .stream()
//                .filter(marketRoleName -> roleAppliesForUser(marketRoleName, user, demand))
//                .collect(toCollection(HashSet::new));
//
//        return userCanPerformActionOverObject(offerStateManager, roleNames, offer);
//    }
//
//    public boolean canRead(User user, Contract contract) {
//
//
//        boolean userIsSigner = user.getMarketRole().stream()
//                .anyMatch(r -> MarketRoleName.SUPPLIER_SIGNER == r || MarketRoleName.CUSTOMER_SIGNER == r);
//
//        if (userIsSigner) {
//            return true;
//        }
//
//        Offer offer = RemoteCallClient.getOffer(chaincodeStub, contract.getOfferId()).get();
//        return canRead(user, offer);
//
//    }
//
//    public boolean canRead(User user, Delivery delivery) {
//
//        boolean userIsPM = user.getMarketRole().stream()
//                .anyMatch(r -> MarketRoleName.SUPPLIER_PROJECT_MANAGER == r);
//
//        if (userIsPM) {
//            return true;
//        }
//
//        Contract contract = RemoteCallClient.getContract(chaincodeStub, delivery.getProjectId()).get();
//        return canRead(user, contract);
//    }
//
//    private boolean userCanPerformActionOverObject(StateManager<?> stateManager, Set<MarketRoleName> roles, BusinessObject object) {
//
//        //Checks if the roles has been accessed
//        Set<MarketRoleName> roleNames = new HashSet<>(roles);
//        boolean roleHasAlreadyAccessed = roleNames.stream()
//                .anyMatch(object::hasRoleAccessed);
//
//        if (roleHasAlreadyAccessed) {
//            return true;
//        }
//
//        Collection<MarketRoleName> availableRoles = stateManager.getRolesWithAvailableActions(object.getState());
//        int max = availableRoles.size() + roleNames.size();
//        roleNames.addAll(availableRoles);
//
//        //If some role intersects, user has access
//        if (max > roleNames.size()) {
//            return true;
//        }
//
//        return object.getState()
//                .getAllowedObjectStateInteractions()
//                .stream()
//                .anyMatch(dsi -> roleNames.contains(dsi.getRole()));
//    }
//
//    /**
//     * Ignore role if is DEMAND_CREATOR and currentUser is not the demand creator. Demand creator can only access
//     * flows related to his own demands
//     *
//     * @param marketRoleName
//     * @param currentUser
//     * @param demand
//     * @return
//     */
//    private boolean roleAppliesForUser(MarketRoleName marketRoleName, User currentUser, Demand demand) {
//        boolean hasDemandCreatorRole = marketRoleName == MarketRoleName.DEMAND_CREATOR;
//        boolean isTheCreatorOfTheDemand = demand.getCreatorId().equals(currentUser);
//        return !(hasDemandCreatorRole && !isTheCreatorOfTheDemand);
//    }
}