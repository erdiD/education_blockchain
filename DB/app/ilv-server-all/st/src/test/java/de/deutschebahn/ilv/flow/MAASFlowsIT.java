package de.deutschebahn.ilv.flow;

import de.deutschebahn.ilv.test.client.DemandClient;
import de.deutschebahn.ilv.test.client.OfferClient;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

import static de.deutschebahn.ilv.businessobject.BOAction.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created by alacambra on 02.06.17.
 */
@Ignore
public class MAASFlowsIT extends MAASScenarioIT {


    @Test
    public void testHappyPath() {

        DemandClient demandClient = new DemandClient();


        //CREATE DEMAND
        demandClient.validateState(tutDemandCreator, "OPENED", SUBMIT_DEMAND, UPDATE, CLOSE_DEMAND);
        demandClient.validateState(tutDemandController, "OPENED");
        demandClient.validateState(tutDemandApproval, "OPENED");

        demandClient.validate(tutOfferCreator, Response.Status.NOT_FOUND);
        demandClient.validate(tutOfferController, Response.Status.NOT_FOUND);
        demandClient.validate(tutOfferApproval, Response.Status.NOT_FOUND);

        //todo UPDATE DEMAND

        //SUBMIT DEMAND IN MARKET
        demandClient.updateStateExpectError(tutDemandApproval, SUBMIT_DEMAND);
        demandClient.updateStateExpectError(tutDemandController, SUBMIT_DEMAND);
        demandClient.updateStateExpectError(tutOfferCreator, SUBMIT_DEMAND);
        demandClient.updateStateExpectError(tutOfferController, SUBMIT_DEMAND);
        demandClient.updateStateExpectError(tutOfferApproval, SUBMIT_DEMAND);

        demandClient.updateState(tutDemandCreator, SUBMIT_DEMAND);

        demandClient.validateState(tutDemandCreator, "SUBMITTED", REVOKE_DEMAND, CLOSE_DEMAND);
        demandClient.validateState(tutDemandApproval, "SUBMITTED");
        demandClient.validateState(tutDemandController, "SUBMITTED");
        demandClient.validateState(tutOfferController, "SUBMITTED", REJECT_DEMAND, ACCEPT_DEMAND);
        demandClient.validateState(tutOfferApproval, "SUBMITTED");
        demandClient.validateState(tutOfferCreator, "SUBMITTED");

        //ACCEPT DEMAND

        demandClient.updateStateExpectError(tutDemandCreator, ACCEPT_DEMAND);
        demandClient.updateStateExpectError(tutDemandApproval, ACCEPT_DEMAND);
        demandClient.updateStateExpectError(tutDemandController, ACCEPT_DEMAND);
        demandClient.updateStateExpectError(tutOfferCreator, ACCEPT_DEMAND);
        demandClient.updateStateExpectError(tutOfferApproval, ACCEPT_DEMAND);

        demandClient.updateState(tutOfferController, ACCEPT_DEMAND);

        demandClient.validateState(tutDemandCreator, "ACCEPTED", REVOKE_DEMAND, CLOSE_DEMAND);
        demandClient.validateState(tutDemandApproval, "ACCEPTED");
        demandClient.validateState(tutDemandController, "ACCEPTED");
        demandClient.validateState(tutOfferController, "ACCEPTED");
        demandClient.validateState(tutOfferApproval, "ACCEPTED");
        demandClient.validateState(tutOfferCreator, "ACCEPTED", MAKE_OFFER);

        //CREATE OFFER

        OfferClient offerClient = new OfferClient();

        offerClient.createExpectErrorStatus(tutOfferController, Response.Status.NOT_ACCEPTABLE);
        offerClient.createExpectErrorStatus(tutOfferApproval, Response.Status.NOT_ACCEPTABLE);
        offerClient.create(tutOfferCreator);

        offerClient.validateState(tutOfferCreator, "OPENED", REVIEW_OFFER, UPDATE);
        offerClient.validateState(tutOfferController, "OPENED");
        offerClient.validateState(tutOfferApproval, "OPENED");
        offerClient.validateNoOffersReceived(tutDemandCreator);
        offerClient.validateNoOffersReceived(tutDemandController);
        offerClient.validateNoOffersReceived(tutDemandApproval);

        demandClient.validateState(tutDemandCreator, "ACCEPTED", REVOKE_DEMAND, CLOSE_DEMAND);
        demandClient.validateState(tutDemandApproval, "ACCEPTED");
        demandClient.validateState(tutDemandController, "ACCEPTED");
        demandClient.validateState(tutOfferController, "ACCEPTED");
        demandClient.validateState(tutOfferApproval, "ACCEPTED");
        demandClient.validateState(tutOfferCreator, "ACCEPTED", MAKE_OFFER);

        //REVIEW OFFER
        offerClient.updateStateExpectError(tutDemandCreator, REVIEW_OFFER);
        offerClient.updateStateExpectError(tutOfferApproval, REVIEW_OFFER);
        offerClient.updateStateExpectError(tutOfferController, REVIEW_OFFER);

        offerClient.updateState(tutOfferCreator, REVIEW_OFFER);

        demandClient.validateState(tutOfferCreator, "ACCEPTED", MAKE_OFFER);
        demandClient.validateState(tutOfferController, "ACCEPTED");
        demandClient.validateState(tutOfferApproval, "ACCEPTED");
        demandClient.validateState(tutDemandCreator, "ACCEPTED", REVOKE_DEMAND, CLOSE_DEMAND);
        demandClient.validateState(tutDemandController, "ACCEPTED");
        demandClient.validateState(tutDemandApproval, "ACCEPTED");

        offerClient.validateState(tutOfferCreator, "WAITING", REVOKE_OFFER, CLOSE_OFFER);
        offerClient.validateState(tutOfferController, "WAITING");
        offerClient.validateState(tutOfferApproval, "WAITING", APPROVE_OFFER);
        offerClient.validateNoOffersReceived(tutDemandCreator);
        offerClient.validateNoOffersReceived(tutDemandController);
        offerClient.validateNoOffersReceived(tutDemandApproval);

        //APPROVE OFFER
        offerClient.updateState(tutOfferApproval, APPROVE_OFFER);

        demandClient.validateState(tutOfferCreator, "ACCEPTED", MAKE_OFFER);
        demandClient.validateState(tutOfferController, "ACCEPTED");
        demandClient.validateState(tutOfferApproval, "ACCEPTED");
        demandClient.validateState(tutDemandCreator, "ACCEPTED", REVOKE_DEMAND, CLOSE_DEMAND);
        demandClient.validateState(tutDemandController, "ACCEPTED");
        demandClient.validateState(tutDemandApproval, "ACCEPTED");

        offerClient.validateState(tutOfferCreator, "APPROVED", SUBMIT_OFFER, CLOSE_OFFER);
        offerClient.validateState(tutOfferController, "APPROVED");
        offerClient.validateState(tutOfferApproval, "APPROVED");
        offerClient.validateNoOffersReceived(tutDemandCreator);
        offerClient.validateNoOffersReceived(tutDemandController);
        offerClient.validateNoOffersReceived(tutDemandApproval);

        //SUBMIT OFFER
        offerClient.updateStateExpectError(tutOfferApproval, SUBMIT_OFFER);
        offerClient.updateStateExpectError(tutOfferController, SUBMIT_OFFER);
        offerClient.updateStateExpectError(tutDemandApproval, SUBMIT_OFFER);
        offerClient.updateStateExpectError(tutDemandCreator, SUBMIT_OFFER);
        offerClient.updateStateExpectError(tutDemandController, SUBMIT_OFFER);

        offerClient.updateState(tutOfferCreator, SUBMIT_OFFER);

        demandClient.validateState(tutOfferCreator, "BLOCKED", MAKE_OFFER);
        demandClient.validateState(tutOfferController, "BLOCKED");
        demandClient.validateState(tutOfferApproval, "BLOCKED");
        demandClient.validateState(tutDemandCreator, "BLOCKED");
        demandClient.validateState(tutDemandController, "BLOCKED", RESIGN_DEMAND);
//        demandClient.validateState(tutDemandApproval, "BLOCKED");

        offerClient.validateState(tutOfferCreator, "OFFERED");
        offerClient.validateState(tutOfferController, "OFFERED");
        offerClient.validateState(tutOfferApproval, "OFFERED");
        offerClient.validateState(tutDemandCreator, "OFFERED");
//        offerClient.validateState(tutDemandController, "OFFERED");
        offerClient.validateState(tutDemandApproval, "OFFERED", ACCEPT_OFFER, REJECT_OFFER);

        //ACCEPT OFFER
        offerClient.updateStateExpectError(tutOfferApproval, ACCEPT_OFFER);
        offerClient.updateStateExpectError(tutOfferController, ACCEPT_OFFER);
        offerClient.updateStateExpectError(tutOfferCreator, ACCEPT_OFFER);
        offerClient.updateStateExpectError(tutDemandCreator, ACCEPT_OFFER);
//        offerClient.updateStateExpectError(tutDemandController, ACCEPT_OFFER);
        offerClient.updateState(tutDemandApproval, ACCEPT_OFFER);

        demandClient.validateState(tutOfferCreator, "COMPLETED");
        demandClient.validateState(tutOfferController, "COMPLETED");
        demandClient.validateState(tutOfferApproval, "COMPLETED");
        demandClient.validateState(tutDemandCreator, "COMPLETED");
        demandClient.validateState(tutDemandController, "COMPLETED");
//        demandClient.validateState(tutDemandApproval, "BLOCKED");

        offerClient.validateState(tutOfferCreator, "ACCEPTED");
        offerClient.validateState(tutOfferController, "ACCEPTED", RESIGN_OFFER);
        offerClient.validateState(tutOfferApproval, "ACCEPTED");
        offerClient.validateState(tutDemandCreator, "ACCEPTED");
//        offerClient.validateState(tutDemandController, "OFFERED");
        offerClient.validateState(tutDemandApproval, "ACCEPTED");

    }

    @Test
    public void t() {
        JsonArray array = new ArrayList<String>().stream().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();

        assertThat(array.size(), is(0));
        JsonObject test = Json.createObjectBuilder().add("arr", array).build();
        assertThat(test.getJsonArray("arr"), not(nullValue()));
    }
}