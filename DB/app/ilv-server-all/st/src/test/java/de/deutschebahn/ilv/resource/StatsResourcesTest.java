package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.businessobject.BOState;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import de.deutschebahn.ilv.test.client.*;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import static de.deutschebahn.ilv.businessobject.BOAction.*;
import static de.deutschebahn.ilv.businessobject.BOState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kschwartz
 */
public class StatsResourcesTest extends ILVScenarioIT {

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
    public void testSubcriptionStats() {
    	
		// Leistungsschein
        createDemandAndInitClients();
        offerClient.setFilename("create-offer-as-subscription.json");
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();

        JsonObject subStatsObject = statsClient.getStats(getWebTarget(MarketRoleName.DEMAND_CREATOR), demandClient.getDemandId());
        
        assertThat( subStatsObject.keySet().toString(), subStatsObject.keySet().contains("totalPaid"), is(true) );
        assertThat( subStatsObject.keySet().toString(), subStatsObject.keySet().contains("totalProgress"), is(true) );
        assertThat( subStatsObject.keySet().toString(), subStatsObject.keySet().contains("budget"), is(true) );
        assertThat( subStatsObject.keySet().toString(), subStatsObject.keySet().contains("duration"), is(true) );
        assertThat( subStatsObject.keySet().toString(), subStatsObject.keySet().contains("paymentsPerMonth"), is(true) );
 
		// Werkvertrag
        createDemandAndInitClients();
        offerClient.setFilename("create-offer.json");
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();
//        runUntilDeliver();
        
        JsonObject wasStatsObject = statsClient.getStats(getWebTarget(MarketRoleName.DEMAND_CREATOR), demandClient.getDemandId());

        assertThat( "wasStatsObject contains totalPaid?", wasStatsObject.keySet().contains("totalPaid"), is(true) );
        assertThat( "wasStatsObject contains totalProgress?", wasStatsObject.keySet().contains("totalProgress"), is(true) );
        assertThat( "wasStatsObject contains budget?", wasStatsObject.keySet().contains("budget"), is(true) );
        assertThat( "wasStatsObject contains duration?", wasStatsObject.keySet().contains("duration"), is(true) );
        assertThat( "wasStatsObject contains paymentsPerMonth?", wasStatsObject.keySet().contains("paymentsPerMonth"), is(true) );
        
     	
    	// Dienstleistungsvertrag
        createDemandAndInitClients();
        offerClient.setFilename("create-offer-as-service-contract.json");
        runUntilOfferOffered();
        runUntilOfferAccepted();
        runUntilContractSigned();
//        runUntilDeliverForServiceContract();
        
        JsonObject scStatsObject = statsClient.getStats(getWebTarget(MarketRoleName.DEMAND_CREATOR), demandClient.getDemandId());
        
        assertThat( "statsObject contains totalPaid?", scStatsObject.keySet().contains("totalPaid"), is(true) );
        assertThat( "scStatsObject contains totalProgress?", scStatsObject.keySet().contains("totalProgress"), is(true) );
        assertThat( "statsObject contains budget?", scStatsObject.keySet().contains("budget"), is(true) );
        assertThat( "statsObject contains duration?", scStatsObject.keySet().contains("duration"), is(true) );
        assertThat( "statsObject contains paymentsPerMonth?", scStatsObject.keySet().contains("paymentsPerMonth"), is(true) );
            
	}

    private void runUntilOfferOffered(OfferClient offerClient) {
        offerClient.setDemandId(demandClient.getDemandId());
        offerClient.create(getWebTarget(MarketRoleName.OFFER_CREATOR));

        offerClient.updateState(getWebTarget(MarketRoleName.OFFER_CREATOR), REVIEW_OFFER);

        offerClient.updateState(getWebTarget(MarketRoleName.SUPPLIER_OFFER_APPROVAL), APPROVE_OFFER_INTERNAL);
        offerClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), OFFERED, RESIGN_OFFER);
        offerClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), OFFERED, ACCEPT_OFFER, REJECT_OFFER);
    }

    private void runUntilDeliverForServiceContract() {
        deliveryClient.loadDeliveryId(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER));
        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), BOState.DELIVERY_ACTIVE, UPLOAD_DELIVERY);
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.DELIVERY_ACTIVE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL), BOState.DELIVERY_ACTIVE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.CUSTOMER_OFFER_TECHNICAL_APPROVAL), BOState.DELIVERY_ACTIVE);
        deliveryClient.validateState(getWebTarget(MarketRoleName.OFFER_CREATOR), BOState.DELIVERY_ACTIVE);
        deliveryClient.validate(anotherDemandCreator.getWebTarget(), Response.Status.FORBIDDEN);

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.DEMAND_CREATOR),
                Response.Status.NOT_ACCEPTABLE,
                null,
                null,
                "./src/test/resources/delivery-for-service-contract.txt"
        );

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.BAD_REQUEST,
                null,
                null,
                "./src/test/resources/delivery-for-service-contract.txt"
        );

        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK, "pspId12");
        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.DEMAND_CREATOR), Response.Status.NOT_ACCEPTABLE, "pspId12");

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.OK,
                5,
                5,
                "./src/test/resources/delivery-for-service-contract.txt"
        );

        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK, "pspId120");
        deliveryClient.addPspAndValidate(getWebTarget(MarketRoleName.OFFER_CREATOR), Response.Status.NOT_ACCEPTABLE, "pspId120");

        deliveryClient.uploadDeliveryAndValidate(
                getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER),
                Response.Status.OK,
                10,
                0,
                "./src/test/resources/delivery-for-service-contract.txt"
        );

        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), DELIVERY_ACTIVE, UPLOAD_DELIVERY);
        JsonObject deliveryJson = deliveryClient.validate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
        assertThat(deliveryJson.containsKey("entries"), CoreMatchers.is(true));

        //TODO: right now duplicates are not controlled. Therefore 15
        assertThat(deliveryJson.getJsonArray("entries").size(), CoreMatchers.is(15));


        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_ACTIVE);
        deliveryJson = deliveryClient.validate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
        assertThat(deliveryJson.containsKey("entries"), CoreMatchers.is(true));

        //TODO: right now duplicates are not controlled. Therefore 15
        assertThat(deliveryJson.getJsonArray("entries").size(), CoreMatchers.is(15));
    }
    
    private void runUntilDeliver() {
        deliveryClient.loadDeliveryId(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER));
        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), BOState.DELIVERY_ACTIVE, UPLOAD_DELIVERY);
        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), BOState.DELIVERY_ACTIVE);
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
                Response.Status.BAD_REQUEST,
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

        deliveryClient.validateState(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), DELIVERY_ACTIVE, UPLOAD_DELIVERY);
        JsonObject deliveryJson = deliveryClient.validate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
        assertThat(deliveryJson.containsKey("entries"), CoreMatchers.is(true));

        //TODO: right now duplicates are not controlled. Therefore 15
        assertThat(deliveryJson.getJsonArray("entries").size(), CoreMatchers.is(15));


        deliveryClient.validateState(getWebTarget(MarketRoleName.DEMAND_CREATOR), DELIVERY_ACTIVE);
        deliveryJson = deliveryClient.validate(getWebTarget(MarketRoleName.SUPPLIER_PROJECT_MANAGER), Response.Status.OK);
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

        @SuppressWarnings("unused")
		String offerId = offerClient.create(getWebTarget(MarketRoleName.OFFER_CREATOR));

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