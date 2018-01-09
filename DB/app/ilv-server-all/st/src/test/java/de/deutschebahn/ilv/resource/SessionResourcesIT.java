package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by alacambra on 02.06.17.
 */
public class SessionResourcesIT extends ILVScenarioIT {


    @Test
    public void sessionCycle() {

        NewCookie sessionCookie1;
        NewCookie sessionCookie2;

        //login
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR)
                .path("session").request().header("Authorization", getRestClient(MarketRoleName.DEMAND_CREATOR)
                        .getUsername())
                .post(Entity.json(Json.createObjectBuilder().build()));

        assertThat(r.getStatus(), is(200));
        sessionCookie1 = NewCookie.valueOf(r.getHeaderString("Set-Cookie"));
        assertThat(sessionCookie1, notNullValue());

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("session").request().get();
        assertThat(r.getStatus(), is(200));
        JsonObject userJson = r.readEntity(JsonObject.class);
        assertThat(userJson.getString("username"), is(getRestClient(MarketRoleName.DEMAND_CREATOR).getUsername()));

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("user").request().get();
        assertThat(r.getStatus(), is(200));

        //Login with another username
        r = getWebTarget(MarketRoleName.DEMAND_CREATOR)
                .path("session").request()
                .header("Authorization", getRestClient(MarketRoleName.SUPPLIER_OFFER_APPROVAL).getUsername())
                .post(Entity.json(Json.createObjectBuilder().build()));

        assertThat(r.getStatus(), is(200));
        sessionCookie2 = NewCookie.valueOf(r.getHeaderString("Set-Cookie"));
        assertThat(sessionCookie2, notNullValue());
        assertThat(sessionCookie2, not(sessionCookie1));

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("session").request().get();
        assertThat(r.getStatus(), is(200));
        userJson = r.readEntity(JsonObject.class);
        assertThat(userJson.getString("username"), is(getRestClient(MarketRoleName.SUPPLIER_OFFER_APPROVAL).getUsername().toLowerCase()));

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("user").request().get();
        assertThat(r.getStatus(), is(200));

        //Delete session
        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("session").request().delete();
        assertThat(r.getStatus(), CoreMatchers.is(204));

        //Next calls are unauthorized
        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("user").request().get();
        assertThat(r.getStatus(), is(401));
    }
}
