package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.business.*;
import de.deutschebahn.ilv.smartcontract.business.authorization.ObjectAccessService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserPrincipalService;
import de.deutschebahn.ilv.smartcontract.business.authorization.UserRoleChecker;
import de.deutschebahn.ilv.smartcontract.business.demand.DemandAccessService;
import de.deutschebahn.ilv.smartcontract.business.demand.DemandFacade;
import de.deutschebahn.ilv.smartcontract.business.demand.DemandILVFlow;
import de.deutschebahn.ilv.smartcontract.business.remote.ChaincodeTargetFactory;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectNotification;
import de.deutschebahn.ilv.smartcontract.business.remote.ObjectUpdatedNotifier;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.AvailableActionsService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.ObjectFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateActionTriggerService;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.StateManager;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DemandDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.GenericActions;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
@Ignore
public class DemandChaincodeTest {

    private ObjectChaincode cut;
    private ChaincodeStub chaincodeStub;
    EntityBuilder<Offer, OfferBuilder> offerBuilder;
    EntityBuilder<Demand, DemandBuilder> demandBuilder;
    EntityBuilder<User, UserBuilder> userBuilder;
    User loggedUser;
    String loggedUserId = "logged-user-id";

    @Before
    public void init() {
        DemandDataConverter dataConverter = new DemandDataConverter();
        ChaincodeTargetFactory chaincodeTargetFactory = new ChaincodeTargetFactory();
        ObjectUpdatedNotifier<Demand> objectUpdatedNotifier = new ObjectUpdatedNotifier<>(chaincodeTargetFactory, dataConverter);
        DataConverterProvider dataConverterProvider = new DataConverterProvider();
        UserRoleChecker.UserRoleCheckerFactory userRoleCheckerFactory = UserRoleChecker::new;
        UserPrincipalService.UserPrincipalServiceFactory userPrincipalServiceFactory = UserPrincipalService::new;
        ObjectFacade.ObjectFacadeFactory<Demand> objectFacadeFactory = DemandFacade::new;
        StateManager.StateManagerFactory<Demand> stateManagerFactory = StateManager::new;
        ObjectFlow statesFlow = new DemandILVFlow();
        AvailableActionsService availableActionsService = new AvailableActionsService(
                statesFlow.getFlowStepsAsSet(),
                userRoleCheckerFactory
        );
        ObjectAccessService.ObjectAccessServiceFactory accessServiceFactory = stub -> new DemandAccessService(availableActionsService, stub, userRoleCheckerFactory);
        StateActionTriggerService.StateActionTriggerServiceFactory<Demand> actionTriggerServiceFactory = (sm, params) -> new StateActionTriggerService<>(dataConverter, sm, params);

        ObjectDependenciesFactory<Demand> objectDependencies = new ObjectDependenciesFactory<>(
                new DemandILVFlow(),
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

        cut = new ObjectChaincode(objectDependencies);
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
        CompositeKey demandKey = IdUtils.generateDemandKey();
        String projectId = IdUtils.getProjectId(demandKey);
        setChaincodeStubMockParams(GenericActions.getById.name(), loggedUserId, demandKey.toString());
        demandBuilder.getBuilder().withId(demandKey.toString()).withProjectId(projectId).withCreatorId(loggedUserId);

        mockInvocation(chaincodeStub, "DemandChaincode", Chaincode.Response.Status.SUCCESS, demandBuilder.asBytes());
        when(chaincodeStub.getState(demandKey.toString())).thenReturn(demandBuilder.asBytes());
        Chaincode.Response response = cut.invoke(chaincodeStub);
        assertThat(response.getStatus(), is(Chaincode.Response.Status.SUCCESS));
        Demand offer = new DemandDataConverter().deserialize(Json.createReader(new ByteArrayInputStream(
                response.getPayload())).readObject(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp
        );

        assertThat(offer.getId(), is(demandKey.toString()));
    }

    @Test
    public void invokeFireAction() {
        CompositeKey demandKey = IdUtils.generateDemandKey();
        String organizationId = UUID.randomUUID().toString();
        userBuilder.getBuilder().withMarketRole(Arrays.asList(MarketRoleName.DEMAND_CREATOR)).withOrganizationId(organizationId);
        demandBuilder.getBuilder().withId(demandKey.toString()).withProjectId(IdUtils.getProjectId(demandKey)).withOrganizationId(organizationId);

        setChaincodeStubMockParams(
                GenericActions.fireAction.name(),
                loggedUserId,
                demandKey.toString(),
                ObjectStateTransitionAction.CREATE_DEMAND.name()
        );

        Chaincode.Response offersResponse = mock(Chaincode.Response.class);
        when(offersResponse.getStatus()).thenReturn(Chaincode.Response.Status.SUCCESS);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add(offerBuilder.makeNew().asJson()).add(offerBuilder.makeNew().asJson()).add(offerBuilder.makeNew().asJson());
        JsonObjectBuilder offersObjectBuilder = Json.createObjectBuilder().add("offers", jsonArrayBuilder);
        when(offersResponse.getPayload()).thenReturn(offersObjectBuilder.build().toString().getBytes());
        when(chaincodeStub.invokeChaincode(eq("Offer"), ArgumentMatchers.any())).thenReturn(offersResponse);
        when(chaincodeStub.getState(demandKey.toString())).thenReturn(demandBuilder.asBytes());

        Chaincode.Response actionResponse = cut.invoke(chaincodeStub);
        assertThat(actionResponse.getStatus(), is(Chaincode.Response.Status.SUCCESS));
        Demand demand = new DemandDataConverter().deserialize(Json.createReader(new ByteArrayInputStream(
                actionResponse.getPayload())).readObject(), DataConverter.DeserializeView.jsonFromFabricToObjectInApp
        );
        assertThat(demand.getId(), is(demandKey.toString()));
    }

    @Test
    public void updateObjectHandler() {
        CompositeKey demandKey = IdUtils.generateDemandKey();
        offerBuilder.getBuilder().withId(IdUtils.generateOfferKey(demandKey, 1).toString()).withProjectId(IdUtils.getProjectId(demandKey));
        demandBuilder.getBuilder().withId(demandKey.toString()).withProjectId(IdUtils.getProjectId(demandKey));
        Chaincode.Response response = mock(Chaincode.Response.class);
        when(response.getStatus()).thenReturn(Chaincode.Response.Status.SUCCESS);

        setChaincodeStubMockParams(
                GenericActions.objectUpdated.name(),
                "",
                new ObjectNotification("Offer", offerBuilder.asJson()).toJsonObject().toString()
        );

        when(chaincodeStub.invokeChaincode(anyString(), ArgumentMatchers.any())).thenReturn(response);
        when(chaincodeStub.getState(demandKey.toString())).thenReturn(demandBuilder.asBytes());
        response = cut.invoke(chaincodeStub);
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
