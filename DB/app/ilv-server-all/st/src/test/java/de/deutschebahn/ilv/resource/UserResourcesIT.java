package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.junit.Test;

import javax.json.JsonArray;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

/**
 * Created by alacambra on 02.06.17.
 */
public class UserResourcesIT extends ILVScenarioIT {

    @Test
    public void fetchUsersTest() {

        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("user").request().get();
        assertThat(r.getStatus(), is(200));
        JsonArray ent = r.readEntity(JsonArray.class);
        assertThat(ent.size(), greaterThan(1));
        System.out.println(ent);
    }
}
