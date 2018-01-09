package de.deutschebahn.ilv.flow;

import de.deutschebahn.ilv.businessobject.BOAction;
import de.deutschebahn.ilv.businessobject.BOState;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.test.client.*;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;

import static de.deutschebahn.ilv.businessobject.BOAction.*;
import static de.deutschebahn.ilv.businessobject.BOState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alacambra on 02.06.17.
 */
public class ILVFlowsIT extends ILVScenarioIT {

    private DemandClient demandClient;
    private OfferClient offerClient;
    private ContractClient contractClient;
    private DeliveryClient deliveryClient;
    private StatsClient statsClient;


    @Before
    public void before() {
        demandClient = new DemandClient();
        offerClient = new OfferClient();
        contractClient = new ContractClient();
        deliveryClient = new DeliveryClient();
        statsClient = new StatsClient();
    }

    @Test
    public void testHappyPath() {
        ProjectClient.setValidationEnabled(true);
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        ProjectClient.setValidationEnabled(true);
        runUntilContractSigned();
        runUntilDeliver();

        JsonObject jsonObject = statsClient.getStats(getWebTarget(MarketRoleName.DEMAND_CREATOR), demandClient.getDemandId());

        assertThat(jsonObject.toString(), jsonObject.getJsonObject("scopePerMonth").size(), is(10));
        assertThat(jsonObject.toString(), jsonObject.getJsonObject("paymentsPerMonth").size(), is(10));
        assertThat(jsonObject.getJsonObject("scopePerMonth").toString(),
                jsonObject.getJsonObject("scopePerMonth").getJsonString("02-2016").getString(), is("35.00"));
        assertThat(jsonObject.getJsonObject("paymentsPerMonth").toString(),
                jsonObject.getJsonObject("paymentsPerMonth").getJsonString("2016-40").getString(), is("5213.23"));
        //TODO: reenable when a new BIGDecimal formatter is avalibale for percentages. Now using the currency one
//        assertThat(jsonObject.getJsonString("totalPaid").toString(), jsonObject.getJsonString("totalPaid").getString(), is("120.50"));
//        assertThat(jsonObject.getJsonString("achieved").toString(), jsonObject.getJsonString("achieved").getString(), is("65.00"));
//        assertThat(jsonObject.getJsonString("budget").toString(), jsonObject.getJsonString("budget").getString(), is("40000.00"));
    }

    @Test
    public void deliveryTest() {

        // Werkvertag
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();


        // Dienstleistungsvertrag
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();

    }

    @Test
    public void getAllDemands() {
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project").request().get();
        assertThat(r.getStatus(), is(200));
        System.out.println(r.readEntity(String.class).toString());
    }

    @Test
    public void testRejectionPath() {
        createDemandAndInitClients();
        runUntilOfferOffered();

        offerClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), REJECT_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), CLOSED);
        demandClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), SUBMITTED, MAKE_OFFER);
        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        demandClient.validate(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), Response.Status.FORBIDDEN);
        offerClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
        demandClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
    }

    @Test
    public void testOffersClosedWhenDemandClosed() {
        createDemandAndInitClients();
        runUntilOfferOffered();

        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.OFFERED, RESIGN_OFFER);

        demandClient.updateStateExpectError(getWebTarget(MarketRoleName.OFFER_CREATOR), CLOSE_DEMAND);
        demandClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), RESIGN_DEMAND);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.CLOSED);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.CLOSED);
    }

    @Test
    public void testDemandAndOfferNotReeditableAfterSubmiting() {
        createDemandAndInitClients();
        runUntilOfferOffered();

        demandClient.verifyUpdate(getWebTarget(MarketRoleName.DEMAND_CREATOR), Response.Status.NOT_ACCEPTABLE);
        offerClient.verifyUpdate(getWebTarget(MarketRoleName.OFFER_CREATOR), Response.Status.NOT_ACCEPTABLE);
    }

    @Test
    public void testSeveralOffersForSameDemandClosedWhenDemandCloses() {
        createDemandAndInitClients();
        runUntilOfferOffered();

        OfferClient offerClient2 = new OfferClient();
        runUntilOfferOffered(offerClient2);

        demandClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), RESIGN_DEMAND);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.CLOSED);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.CLOSED);
        offerClient2.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.CLOSED);
    }

    @Test
    public void testSeveralOffersForSameDemandOnlyOneCanBeAccepted() {
        createDemandAndInitClients();
        runUntilOfferOffered();

        OfferClient offerClient2 = new OfferClient();
        runUntilOfferApprovedAndAccepted(offerClient2);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.COMPLETED, RESIGN_DEMAND);
        offerClient2.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.COMPLETED, RESIGN_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.CLOSED);
    }

    private void runUntilOfferApprovedAndAccepted(OfferClient offerClient) {
        runUntilOfferOffered(offerClient);
        offerClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), ACCEPT_OFFER);
        offerClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), APPROVE_OFFER);
        offerClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), APPROVE_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), COMPLETED, RESIGN_OFFER);
    }

    private void runUntilOfferOffered(OfferClient offerClient) {
        offerClient.setDemandId(demandClient.getDemandId());
        offerClient.create(getWebTarget(MarketRoleName.OFFER_CREATOR));

        offerClient.updateState(getWebTarget(MarketRoleName.OFFER_CREATOR), REVIEW_OFFER);

        offerClient.updateState(getWebTarget(MarketRoleName.SUPPLIER_OFFER_APPROVAL), APPROVE_OFFER_INTERNAL);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), OFFERED, RESIGN_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), OFFERED, ACCEPT_OFFER, REJECT_OFFER);
    }

    @Test
    public void testContractCanNotBeCancelledForASignerPartyEventNotSignedForTheOther() {
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        contractClient.loadContractId(getWebTarget(MarketRoleName.SUPPLIER_SIGNER));

        contractClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), CONTRACT_CREATED, SIGN_CONTRACT, REJECT_CONTRACT);
        contractClient.updateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), SIGN_CONTRACT);
        contractClient.updateStateExpectError(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), REJECT_CONTRACT);

        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), CONTRACT_SUPPLIER_SIGNED, SIGN_CONTRACT, REJECT_CONTRACT);
        contractClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), REJECT_CONTRACT);
    }

    @Test
    @Ignore
    public void testDeliveryNotAcceptedWhenNotInDateRangeOrInFuture() throws IOException {
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();
        deliveryClient.loadDeliveryId(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER));
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_ACTIVE);

        String deliveryFuture = "pspId12#01-08-2018#1213,23#25";
        String deliveryNotStarted = "pspId12#01-08-2010#1213,23#25";
        String deliveryOk = "pspId12#01-08-2017#1213,23#25";
        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK, "pspId12");
        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Arrays.asList(deliveryFuture, deliveryOk, deliveryNotStarted),
                1,
                2);

    }

    @Test
    public void testNotPossibleToCancelDemandNorOfferWhenContractSigned() {
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();

        offerClient.updateStateExpectError(getWebTarget(MarketRoleName.OFFER_CREATOR), CLOSE_OFFER);
        demandClient.updateStateExpectError(getWebTarget(MarketRoleName.DEMAND_CREATOR), CLOSE_DEMAND);
    }

    @Test
    @Ignore
    public void testTerminateContractStopsDelivery() {
        createDemandAndInitClients();
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();

        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_ACTIVE);
        contractClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), TERMINATE_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), CONTRACT_TERMINATED);
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_CLOSED);

    }

    private void runUntilDeliver() {
        deliveryClient.loadDeliveryId(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER));
        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), BOState.DELIVERY_ACTIVE, UPLOAD_DELIVERY, CREATE_DELIVERY_ENTRY, UPDATE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.DELIVERY_ACTIVE, BOAction.CLOSE_DELIVERY);
        deliveryClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), BOState.DELIVERY_ACTIVE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), BOState.DELIVERY_ACTIVE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.DELIVERY_ACTIVE);
        deliveryClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.DEMAND_CREATOR),
                Response.Status.NOT_ACCEPTABLE,
                null,
                null,
                "./src/test/resources/delivery.txt"
        );

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.NOT_ACCEPTABLE,
                null,
                null,
                "./src/test/resources/delivery.txt"
        );

        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK, "pspId12");
        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.DEMAND_CREATOR), Response.Status.NOT_ACCEPTABLE, "pspId12");

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.OK,
                5,
                5,
                "./src/test/resources/delivery.txt"
        );

        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK, "pspId120");
        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.OFFER_CREATOR), Response.Status.NOT_ACCEPTABLE, "pspId120");

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.OK,
                10,
                0,
                "./src/test/resources/delivery.txt"
        );

        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), DELIVERY_ACTIVE, UPLOAD_DELIVERY, CREATE_DELIVERY_ENTRY, UPDATE);
        JsonObject deliveryJson = deliveryClient.get(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
        assertThat(deliveryJson.containsKey("entries"), CoreMatchers.is(true));

        //TODO: right now duplicates are not controlled. Therefore 15
        assertThat(deliveryJson.getJsonArray("entries").size(), CoreMatchers.is(15));


        //TODO recheck demand creator can close delivery
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_ACTIVE, CLOSE_DELIVERY);
        deliveryJson = deliveryClient.get(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
        assertThat(deliveryJson.containsKey("entries"), CoreMatchers.is(true));

        //TODO: right now duplicates are not controlled. Therefore 15
        assertThat(deliveryJson.getJsonArray("entries").size(), CoreMatchers.is(15));
    }

    private void createDemandAndInitClients() {
        String demandId = demandClient.create(getWebTarget(MarketRoleName.DEMAND_CREATOR));
        offerClient.setDemandId(demandId);
        contractClient.setDemandId(demandId);
        deliveryClient.setDemandId(demandId);
    }

    private void runUntilOfferOffered() {
        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.OPENED, SUBMIT_DEMAND, UPDATE, CLOSE_DEMAND);

        demandClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMIT_DEMAND);
        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        demandClient.validate(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), Response.Status.FORBIDDEN);
        demandClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), SUBMITTED, MAKE_OFFER);

        offerClient.create(getWebTarget(MarketRoleName.OFFER_CREATOR));
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), OPENED, UPDATE, REVIEW_OFFER, CLOSE_OFFER);
        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        offerClient.validateNoOffersReceived(getWebTarget(MarketRoleName.DEMAND_CREATOR));
        offerClient.validate(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), Response.Status.FORBIDDEN);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        demandClient.validate(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), Response.Status.FORBIDDEN);

        demandClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), SUBMITTED, MAKE_OFFER);

        //---------------------------------------------------------

        offerClient.updateStateExpectError(getWebTarget(MarketRoleName.OFFER_CREATOR), SUBMIT_OFFER);
        offerClient.updateStateExpectError(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), SUBMIT_OFFER);
        offerClient.updateState(getWebTarget(MarketRoleName.OFFER_CREATOR), REVIEW_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), WAITING, CLOSE_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_OFFER_APPROVAL), WAITING, APPROVE_OFFER_INTERNAL, REVOKE_OFFER);
        offerClient.validate(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), Response.Status.FORBIDDEN);
        offerClient.validate(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), Response.Status.FORBIDDEN);
        offerClient.validate(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), Response.Status.FORBIDDEN);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        offerClient.validateNoOffersReceived(getWebTarget(MarketRoleName.DEMAND_CREATOR));

        //---------------------------------------------------------
        offerClient.updateState(getWebTarget(MarketRoleName.SUPPLIER_OFFER_APPROVAL), APPROVE_OFFER_INTERNAL);
        offerClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_OFFER_APPROVAL), OFFERED);

        offerClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
        demandClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
    }

    private void runUntilOfferAccepted() {
        offerClient.updateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), ACCEPT_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), ACCEPTED, RESIGN_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), ACCEPTED);
        offerClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), ACCEPTED, APPROVE_OFFER, REJECT_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), ACCEPTED, APPROVE_OFFER, REJECT_OFFER);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), SUBMITTED, RESIGN_DEMAND);
        demandClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), SUBMITTED);
        demandClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), SUBMITTED, MAKE_OFFER);


        //---------------------------------------------------------

        offerClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), APPROVE_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), COMM_APPROVED);
        offerClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), COMM_APPROVED);
        offerClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), COMM_APPROVED, APPROVE_OFFER, REJECT_OFFER);

        OfferClient offerClient2 = new OfferClient();
        runUntilOfferOffered(offerClient2);

        offerClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), APPROVE_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), COMPLETED, RESIGN_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), COMPLETED);
        offerClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), COMPLETED);

        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), COMPLETED, RESIGN_DEMAND);
        demandClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), COMPLETED);
        demandClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), COMPLETED);
        offerClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
        demandClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);

        //TODO: Check close event successfully implemented
        offerClient2.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), CLOSED);
    }

    private void runUntilContractSigned() {
        contractClient.loadContractId(getWebTarget(MarketRoleName.SUPPLIER_SIGNER));

        contractClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), CONTRACT_CREATED, SIGN_CONTRACT, REJECT_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), CONTRACT_CREATED, SIGN_CONTRACT, REJECT_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), CONTRACT_CREATED);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), CONTRACT_CREATED);
        contractClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), CONTRACT_CREATED);

        contractClient.updateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), SIGN_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), CONTRACT_CLIENT_SIGNED, SIGN_CONTRACT, REJECT_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), CONTRACT_CLIENT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), CONTRACT_CLIENT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), CONTRACT_CLIENT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), CONTRACT_CLIENT_SIGNED);

        contractClient.updateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), SIGN_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_SIGNER), CONTRACT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_SIGNER), CONTRACT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), CONTRACT_SIGNED, TERMINATE_CONTRACT);
        contractClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), CONTRACT_SIGNED);
        contractClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), CONTRACT_SIGNED);
        demandClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), LOCKED);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), LOCKED);

        contractClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
        offerClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
        demandClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);
    }

}