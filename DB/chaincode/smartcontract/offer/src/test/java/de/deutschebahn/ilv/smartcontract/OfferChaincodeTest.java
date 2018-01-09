package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectNotification;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.OfferChaincodeAction;
import de.deutschebahn.ilv.smartcontract.commons.OfferDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.SerializationHelper;
import de.deutschebahn.ilv.smartcontract.offer.OfferAccessService;
import de.deutschebahn.ilv.smartcontract.offer.OfferFacade;
import de.deutschebahn.ilv.smartcontract.offer.OfferILVFlow;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import javax.json.Json;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
@Ignore
public class OfferChaincodeTest {

    private ObjectChaincode cut;
    private ChaincodeStub chaincodeStub;
    EntityBuilder<Offer, OfferBuilder> offerBuilder;
    EntityBuilder<Demand, DemandBuilder> demandBuilder;
    EntityBuilder<User, UserBuilder> userBuilder;
    User loggedUser;
    String loggedUserId = "logged-user-id";

    @Before
    public void init() {
        OfferDataConverter dataConverter = new OfferDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Offer> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Offer> objectFacadeFactory = OfferFacade::new;
        StateManager.StateManagerFactory<Offer> stateManagerFactory = StateManager::new;
        ObjectFlow objectFlow = new OfferILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(objectFlow.getFlowStepsAsSet(), userRoleCheckerFactory);
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new OfferAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Offer> actionTriggerServiceFactory = (sm, params) -> new StateActionTriggerService<>(dataConverter, sm, params);

        ObjectDependenciesFactory<Offer> dependenciesFactory = new ObjectDependenciesFactory<>(
                objectFlow,
                dataConverter,
                chaincodeTargetFactory,
                objectUpdatedNotifier,
                dataConverterProvider,
                userRoleCheckerFactory,
                accessServiceFactory,
                userPrincipalServiceFactory,
                objectFacadeFactory,
                stateManagerFactory,
                availableActionsService,
                actionTriggerServiceFactory);

        cut = new ObjectChaincode(dependenciesFactory);
        chaincodeStub = mock(ChaincodeStub.class);
        offerBuilder = EntityBuilder.createOffer();
        demandBuilder = EntityBuilder.createDemand();
        userBuilder = EntityBuilder.createUser();
        loggedUser = userBuilder.getEntity();
    }

    @Test
    public void invokeInit() {
        Chaincode.Response response = cut.init(chaincodeStub);
        assertThat(response.getStatus(), is(Chaincode.Response.Status.SUCCESS));
    }

    @Test
    public void invokeGetById() {
        CompositeKey offerKey = IdUtils.generateOfferKey(IdUtils.generateDemandKey(), 1);
        String projectId = IdUtils.getProjectId(offerKey);
        setChaincodeStubMockParams(OfferChaincodeAction.getById.name(), loggedUserId, offerKey.toString());
        offerBuilder.getBuilder().withProjectId(projectId).withId(offerKey.toString());
        demandBuilder.getBuilder().withProjectId(projectId).withId(IdUtils.recreateDemandId(projectId));

        mockInvocation(chaincodeStub, "DemandChaincode", Chaincode.Response.Status.SUCCESS, demandBuilder.asBytes());
        Chaincode.Response response = cut.invoke(chaincodeStub);
        assertThat(response.getStatus(), is(Chaincode.Response.Status.SUCCESS));
        Offer offer = new OfferDataConverter().deserialize(Json.createReader(new ByteArrayInputStream(
                response.getPayload())).readObject(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp
        );

        assertThat(offer.getId(), is(offerKey.toString()));
    }

    @Test
    public void invokeFireAction() {
        CompositeKey offerKey = IdUtils.generateOfferKey(IdUtils.generateDemandKey(), 1);
        String orgId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        userBuilder.getBuilder().withId(userId).withOrganizationId(orgId);

        demandBuilder.getBuilder()
                .withId(IdUtils.recreateDemandId(offerBuilder.getEntity().getProjectId()))
                .withProjectId(IdUtils.getProjectId(offerKey))
                .withOrganizationId(orgId);

        Chaincode.Response demandResponse = mock(Chaincode.Response.class);
        when(demandResponse.getStatus()).thenReturn(Chaincode.Response.Status.SUCCESS);
        when(demandResponse.getPayload()).thenReturn(demandBuilder.asBytes());
        when(chaincodeStub.invokeChaincode(eq("Demand"), ArgumentMatchers.any())).thenReturn(demandResponse);

        offerBuilder.getBuilder().withId(offerKey.toString())
                .withProjectId(IdUtils.getProjectId(offerKey))
                .withOrganizationId(orgId);

        when(chaincodeStub.getState(offerKey.toString())).thenReturn(offerBuilder.asBytes());

        setChaincodeStubMockParams(
                OfferChaincodeAction.fireAction.name(),
                userId,
                offerKey.toString(),
                ObjectStateTransitionAction.OPEN_OFFER.name()
        );

        Chaincode.Response actionResponse = cut.invoke(chaincodeStub);
        assertThat(actionResponse.getStatus(), is(Chaincode.Response.Status.SUCCESS));
        Offer offer = new OfferDataConverter().deserialize(Json.createReader(new ByteArrayInputStream(
                actionResponse.getPayload())).readObject(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp
        );

        assertThat(offer.getId(), is(offerKey.toString()));
    }

    @Test
    public void updateObjectHandler() {
        String userId = UUID.randomUUID().toString();
        CompositeKey offerKey = IdUtils.generateOfferKey(IdUtils.generateDemandKey(), 1);
        offerBuilder.getBuilder().withId(offerKey.toString()).withProjectId(IdUtils.getProjectId(offerKey));

        setChaincodeStubMockParams(
                OfferChaincodeAction.objectUpdated.name(),
                userId,
                new ObjectNotification("Offer", SerializationHelper.bytesToJsonObject(offerBuilder.asBytes())).toJsonObject().toString()
        );

        Chaincode.Response response = cut.invoke(chaincodeStub);
        assertThat(response.getStatus(), is(Chaincode.Response.Status.SUCCESS));
    }

    private void setChaincodeStubMockParams(String method, String... params) {
        when(chaincodeStub.getFunction()).thenReturn(method);
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList(params));

        userBuilder.getBuilder().withId(loggedUserId);
        mockInvocation(chaincodeStub, "UserServiceChaincode", Chaincode.Response.Status.SUCCESS, userBuilder.asBytes());
        loggedUser = userBuilder.getEntity();
    }

    private Chaincode.Response mockInvocation(ChaincodeStub chaincodeStub, String chaincodeName, Chaincode.Response.Status status, byte[] responseBytes) {
        Chaincode.Response response = mock(Chaincode.Response.class);
        when(response.getStatus()).thenReturn(status);
        when(response.getPayload()).thenReturn(responseBytes);

        when(chaincodeStub.invokeChaincode(eq(chaincodeName), any())).thenReturn(response);

        return response;
    }
}
