package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.domain.User;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTarget;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class RemoteObjectAccess {

    private final String chaincodeName;
    private final ChaincodeStub chaincodeStub;

    public RemoteObjectAccess(String chaincodeName, ChaincodeStub chaincodeStub) {
        this.chaincodeName = chaincodeName;
        this.chaincodeStub = chaincodeStub;
    }

    public boolean canRead(User user, String objectId) {
        return new ChaincodeTarget(chaincodeName)
                .withChaincodeStub(chaincodeStub)
                .function(GenericActions.canRead.name())
                .asUser(user.getId())
                .params(objectId)
                .build()
                .execute(jsonObject -> jsonObject.containsKey("allowed") && jsonObject.getBoolean("allowed"))
                .orElse(false);
    }

//    public boolean someRoleCanPerformActionOverObject(String userId, String objectId, Set<MarketRoleName> roles) {
//        return new ChaincodeTarget(chaincodeName)
//                .withChaincodeStub(chaincodeStub)
//                .function("someRoleCanPerformActionOverObject")
//                .params(objectId, roles.stream().map(MarketRoleName::name).collect(Collectors.joining(",")))
//                .asUser(userId)
//                .build()
//                .execute(jsonObject -> jsonObject.containsKey("allowed") && jsonObject.getBoolean("allowed"))
//                .get();
//    }

    /**
     * returns if there is some available action for some of the given roles over the final object/s. The object will
     * be extrapolated of the projectId. In case there are several objects of the represented type, it is responsability
     * of the remote chaincode to decide which object/s to check
     *
     * @param userId
     * @param projectId
     * @param roles
     * @return
     */
//    public boolean someRoleCanPerformActionOverProjectObject(String userId, String projectId, Collection<MarketRoleName> roles) {
//
//        StringList stringList = new StringList(roles.stream().map(MarketRoleName::name).collect(Collectors.toList()));
//
//        return new ChaincodeTarget(chaincodeName)
//                .withChaincodeStub(chaincodeStub)
//                .function(GenericActions.someRoleCanPerformActionOverProjectObject.name())
//                .asUser(userId)
//                .params(projectId, stringList.toJson().toString())
//                .build()
//                .execute(Access::new)
//                .map(Access::getValue)
//                .orElse(false);
//    }
}
