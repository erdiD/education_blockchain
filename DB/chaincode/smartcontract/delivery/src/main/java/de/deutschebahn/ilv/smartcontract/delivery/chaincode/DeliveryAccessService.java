package de.deutschebahn.ilv.smartcontract.delivery.chaincode;

import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class DeliveryAccessService extends ObjectAccessService<Delivery> {
    private static final Logger logger = Logger.getLogger(DeliveryAccessService.class.getName());

    public DeliveryAccessService(AvailableActionsService availableActionsService, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory checkerFactory) {
        super(availableActionsService, chaincodeStub, checkerFactory);
    }

    @Override
    protected boolean canReadImpl(User user, Delivery delivery) {

        logger.info(String.format("[canRead] Checking read action. # UserId=%s, userRoles=%s, deliveryId=%s", user.getId(), user.getMarketRole(), delivery.getId()));

        boolean hasAvailableActions = !getAvailableActionsService().getAvailableActions(user, delivery).isEmpty();
        if (hasAvailableActions) {
            return true;
        }

        boolean userHasAlreadyAccessed = RemoteCallClient.userHasAlreadyAccessed(chaincodeStub, user.getId(), delivery.getProjectId());

        if (userHasAlreadyAccessed) {
            return true;
        }

        return false;
    }
}
