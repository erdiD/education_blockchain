package de.deutschebahn.ilv.flow;

import de.deutschebahn.ilv.RequestResponseFilter;
import de.deutschebahn.ilv.TestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by alacambra on 03.06.17.
 */
public abstract class MAASScenarioIT {

    protected static Client cutDemandCreator;
    protected static WebTarget tutDemandCreator;
    protected static Client cutOfferCreator;
    protected static WebTarget tutOfferCreator;

    protected static Client cutDemandController;
    protected static WebTarget tutDemandController;
    protected static Client cutOfferController;
    protected static WebTarget tutOfferController;

    protected static Client cutDemandApproval;
    protected static WebTarget tutDemandApproval;
    protected static Client cutOfferApproval;
    protected static WebTarget tutOfferApproval;

    private static final String END_POINT_LOCAL = "http://localhost:8080/app/resources/";
    private static final String END_POINT_MATILDA = "http://matilda.dbe.aws.db.de:8080/app/resources/";
    private static final String END_POINT = END_POINT_LOCAL;

    @BeforeClass
    public static void setUp() {
        cutDemandCreator = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutDemandCreator = cutDemandCreator.target(END_POINT);

        cutOfferCreator = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutOfferCreator = cutOfferCreator.target(END_POINT);

        cutDemandController = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutDemandController = cutDemandController.target(END_POINT);

        cutOfferController = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutOfferController = cutOfferController.target(END_POINT);

        cutDemandApproval = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutDemandApproval = cutDemandApproval.target(END_POINT);

        cutOfferApproval = ClientBuilder.newClient().register(new RequestResponseFilter());
        tutOfferApproval = cutOfferApproval.target(END_POINT);

        TestUtils.loginUser(tutDemandCreator, "christian");
        TestUtils.loginUser(tutDemandApproval, "volker");
        tutDemandController = tutDemandApproval;

        TestUtils.loginUser(tutOfferCreator, "steffen");
        TestUtils.loginUser(tutOfferController, "sorin");
        TestUtils.loginUser(tutOfferApproval, "sonja");
    }

    @AfterClass
    public static void tearDown() {
        Response r = tutDemandCreator.path("user").request().get();
        assertThat(r.getStatus(), is(Response.Status.OK.getStatusCode()));
        r.close();
        r = tutOfferCreator.path("user").request().get();
        assertThat(r.getStatus(), is(Response.Status.OK.getStatusCode()));
        r.close();
        cutDemandApproval.close();
        cutDemandController.close();
        cutDemandCreator.close();
        cutOfferApproval.close();
        cutOfferController.close();
        cutOfferCreator.close();
    }
}
