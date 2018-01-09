package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.smartcontract.business.ChaincodeName;
import de.deutschebahn.ilv.smartcontract.business.IdUtils;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import de.deutschebahn.ilv.smartcontract.commons.ProjectChaincodeAction;
import de.deutschebahn.ilv.smartcontract.commons.model.BooleanMessage;
import de.deutschebahn.ilv.smartcontract.commons.model.ProjectField;
import de.deutschebahn.ilv.smartcontract.commons.model.StringMessage;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by AlbertLacambraBasil on 11.10.2017.
 */
public class RemoteCallClient {

    public static final String peerId = "peer";

    public static boolean userHasAlreadyAccessed(ChaincodeStub stub, String userId, String projectId) {
        Objects.requireNonNull(userId);
        return new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(ProjectChaincodeAction.userHasAccessedObject.name())
                .params(Arrays.asList(projectId))
                .asUser(userId)
                .build()
                .execute(jsonObject -> new BooleanMessage(jsonObject).getValue())
                .orElse(false);
    }

    public static Chaincode.Response notifyObjectAccessed(ChaincodeStub stub, String userId, String projectId) {
        Objects.requireNonNull(userId);
        Chaincode.Response response = new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(ProjectChaincodeAction.addAllowedReadUser.name())
                .params(Arrays.asList(projectId))
                .asUser(userId)
                .build()
                .execute();

        return response;
    }

    public static boolean canPerformActionOverProjectOffer(ChaincodeStub stub, String userId, String projectId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(projectId);
        return new ChaincodeTarget(ChaincodeName.OFFER_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.canPerformDirectActionOnProjectOffers.name())
                .params(Collections.singletonList(new StringMessage(projectId).toJson().toString()))
                .asUser(userId)
                .build()
                .execute(jsonObject -> new BooleanMessage(jsonObject).getValue())
                .orElse(false);
    }

    public static boolean canPerformActionOverProjectDelivery(ChaincodeStub stub, String userId, String projectId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(projectId);
        String deliveryId = IdUtils.recreateDeliveryId(projectId);
        return new ChaincodeTarget(ChaincodeName.DELIVERY_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.canFireAction.name())
                .params(Collections.singletonList(new StringMessage(deliveryId).toJson().toString()))
                .asUser(userId)
                .build()
                .execute(jsonObject -> new BooleanMessage(jsonObject).getValue())
                .orElse(false);
    }

    public static boolean canPerformActionOverProjectContract(ChaincodeStub stub, String userId, String projectId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(projectId);
        String contractId = IdUtils.recreateContractId(projectId);
        return new ChaincodeTarget(ChaincodeName.CONTRACT_CC)
                .withChaincodeStub(stub)
                .function(GenericActions.canFireAction.name())
                .params(Collections.singletonList(new StringMessage(contractId).toJson().toString()))
                .asUser(userId)
                .build()
                .execute(jsonObject -> new BooleanMessage(jsonObject).getValue())
                .orElse(false);
    }

    public static Chaincode.Response saveProjectField(ChaincodeStub stub, String userId, String projectId, String fieldName, String value) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(projectId);
        return new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(ProjectChaincodeAction.setProjectField.name())
                .params(projectId, new ProjectField(fieldName, value).toJson().toString())
                .asUser(userId)
                .build()
                .execute();
    }

    public static <T> Optional<String> getProjectField(ChaincodeStub stub, String userId, String projectId, String fieldName) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(projectId);
        return new ChaincodeTarget(ChaincodeName.PROJECT_CC)
                .withChaincodeStub(stub)
                .function(ProjectChaincodeAction.getProjectField.name())
                .params(projectId, fieldName)
                .asUser(userId)
                .build()
                .execute(jsonObject -> new ProjectField(jsonObject).getValue());
    }
}
