package de.deutschebahn.ilv.app.demand;

import de.deutschebahn.ilv.TestUtils;
import de.deutschebahn.ilv.app.offer.OfferDataConverter;
import de.deutschebahn.ilv.app.organization.OrganizationDataConverter;
import de.deutschebahn.ilv.app.user.UserDataConverter;
import de.deutschebahn.ilv.domain.Demand;
import de.deutschebahn.ilv.domain.Priority;
import org.junit.Before;
import org.junit.Test;

import javax.json.JsonObject;
import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 08.06.2017.
 */
public class DemandDataConverterTest {

    DemandDataConverter cut;

    @Before
    public void setUp() throws Exception {
        cut = new DemandDataConverter();
        cut.offerDataConverter = new OfferDataConverter();
        cut.organizationDataConverter = new OrganizationDataConverter();
        cut.userDataConverter = new UserDataConverter();
    }

    @Test
    public void deserialize() throws Exception {

        JsonObject demandJson = TestUtils.getJsonObjectFromFile("create-demand.json");
        Demand demand = cut.deserialize(demandJson);
        assertThat(demand.getDescription(), is("Beschreibungstext"));
        assertThat(demand.getBudget(), is(new BigDecimal("40000")));
        assertThat(demand.getPriority(), is(Priority.HIGH));
        assertThat(demand.getName(), is("Ein neues Projekt"));


        demandJson = TestUtils.getJsonObjectFromFile("update-demand.json");
        demand = cut.deserialize(demandJson);
        assertThat(demand.getDescription(), is("new description"));
        assertThat(demand.getBudget(), is(new BigDecimal("100.10")));
        assertThat(demand.getPriority(), is(Priority.HIGH));
        assertThat(demand.getName(), is("updated name"));

        demandJson = TestUtils.getJsonObjectFromFile("failing-create-demand.json");
        demand = cut.deserialize(demandJson);
    }

    @Test
    public void serialize() throws Exception {
    }

}