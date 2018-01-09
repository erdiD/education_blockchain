package de.deutschebahn.ilv.smartcontract.contract.chaincode;

import de.deutschebahn.ilv.domain.Contract;
import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.RemoteCallClient;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class ContractAccessService extends ObjectAccessService<Contract> {

    public ContractAccessService(AvailableActionsService availableActionsService, ChaincodeStub chaincodeStub, UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory) {
        super(availableActionsService, chaincodeStub, userRoleCheckerFactory);
    }

    @Override
    protected boolean canReadImpl(User user, Contract contract) {

        boolean hasAvailableActions = getAvailableActionsService().canPerformSomeAction(user, contract);

        if (hasAvailableActions) {
            return true;
        }

        boolean userHasAlreadyAccessed = RemoteCallClient.userHasAlreadyAccessed(chaincodeStub, user.getId(), contract.getProjectId());

        if (userHasAlreadyAccessed) {
            return true;
        }

        boolean userHasActionsOnDelivery = RemoteCallClient.canPerformActionOverProjectDelivery(chaincodeStub, user.getId(), contract.getProjectId());

        if (userHasActionsOnDelivery) {
            return true;
        }

        return false;
    }
}
