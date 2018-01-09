package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by alacambra on 02.06.17.
 */
public class OrganizationResourcesIT extends ILVScenarioIT {

    @Test
    public void fetchOrganizationsTest() {
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("organization").request().get();
        assertThat(r.getStatus(), is(200));
    }
}
