package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.TestUtils;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static de.deutschebahn.ilv.TestUtils.getJsonEntity;
import static de.deutschebahn.ilv.businessobject.BOAction.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by alacambra on 02.06.17.
 */
public class DemandResourcesIT extends ILVScenarioIT {

    @Test
    public void createDemandCycleNoDirectSubmit() throws IOException {

        String id = createDemand();

        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project/{id}").resolveTemplate("id", id).request().get();
        JsonObject entity = getJsonEntity(r).getJsonObject("demand");
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(entity.toString(), entity, CoreMatchers.notNullValue());
        assertThat(entity.getString("state"), is("OPENED"));
        assertThat(entity.containsKey("availableActions"), is(true));
        JsonArray availableActions = entity.getJsonArray("availableActions");
        assertThat(availableActions, TestUtils.containsAllStates(UPDATE, SUBMIT_DEMAND, CLOSE_DEMAND));
    }

    @Test
    public void attachmentRoundTrip() throws IOException {
        String demandId = createDemand();
        File file = new File("./src/test/resources/user.json");

        long originalSize = file.length();

        final FileDataBodyPart filePart = new FileDataBodyPart("attachment", file);
        final MultiPart multiPartEntity = new FormDataMultiPart().bodyPart(filePart);
        Response response = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand/{id}/attachment").resolveTemplate("id", demandId).request()
                .post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));

        System.out.println("response = " + response.readEntity(String.class));
        
        assertThat(response.getStatus(), is(200));
        response = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project/{id}").resolveTemplate("id", demandId).request().get();
        assertThat(response.getStatus(), is(200));
        JsonObject entity = getJsonEntity(response);

        assertThat(entity.getJsonObject("demand").getJsonArray("attachments"), notNullValue());
        
		boolean foundAttachment = entity.getJsonObject("demand").getJsonArray("attachments").stream()
				.anyMatch(o -> ((JsonObject) o).containsKey("user.json"));
		assertThat(foundAttachment, is(true));
        
        Optional<JsonObject> fileObjectOptional = entity.getJsonObject("demand").getJsonArray("attachments").stream()
    			.filter( o -> ((JsonObject) o).containsKey("user.json"))
    	        .findFirst().map( v -> (JsonObject) v); 
        
        JsonObject foundJsonObject = fileObjectOptional.orElse(Json.createObjectBuilder().build());
        JsonObject fileInfoObject = foundJsonObject.getJsonArray("user.json").getJsonObject(0);
        String fileId = fileInfoObject.getString("fileId");
        
		response = getWebTarget(MarketRoleName.DEMAND_CREATOR)
                .path("demand/{id}/attachment/{fileId}")
                .resolveTemplate("id", demandId)
                .resolveTemplate("fileId", fileId)
                .request()
                .get();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_OCTET_STREAM_TYPE));
        DataInputStream inputStream = new DataInputStream(response.readEntity(InputStream.class));
        assertThat((long) inputStream.available(), is(originalSize));
    }

    private String createDemand() {
        JsonObject demand = TestUtils.getJsonObjectFromFile("create-demand.json");
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand").request().post(Entity.json(demand));
        JsonObject entity = getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        return TestUtils.extractId(entity);
    }

    @Test
    public void createDemandCycleDirectSubmit() {

        JsonObject project = TestUtils.getJsonObjectFromFile("create-demand.json");

        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand").queryParam("directSubmit", true).request().post(Entity.json(project));
        JsonObject entity = r.readEntity(JsonObject.class);
        assertThat(entity.toString(), r.getStatus(), is(201));
        String id = TestUtils.extractId(entity);

        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project/{id}").resolveTemplate("id", id).request().get();
        entity = r.readEntity(JsonObject.class).getJsonObject("demand");
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(entity.toString(), entity, CoreMatchers.notNullValue());
        assertThat(entity.getString("state"), is("SUBMITTED"));
    }

    @Test
    public void updateDemand() {

        JsonObject project = TestUtils.getJsonObjectFromFile("create-demand.json");
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand").request().post(Entity.json(project));
        JsonObject entity = getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(201));
        String id = TestUtils.extractId(entity);

        JsonObject jsonObject = TestUtils.getJsonObjectFromFile("update-demand.json");
        r = getWebTarget(MarketRoleName.CUSTOMER_OFFER_COMMERCIAL_APPROVAL).path("demand/{id}").resolveTemplate("id", id).request().put(Entity.json(jsonObject));
//        assertThat(r.getStatus(), is(Response.Status.NOT_ACCEPTABLE.getStatusCode()));
        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand/{id}").resolveTemplate("id", id).request().put(Entity.json(jsonObject));
        entity = getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project/{id}").resolveTemplate("id", id).request().get();
        entity = getJsonEntity(r).getJsonObject("demand");
        assertThat(entity.toString(), entity.getString("name"), is("updated name"));
        assertThat(entity.toString(), entity.getString("endDate"), is("25.11.2017"));
        assertThat(entity.toString(), entity.getString("budget"), is("1.010,10"));
        assertThat(entity.toString(), entity.getString("priority"), is("LOW"));
    }


    @Test
    public void fetchProjectsTest() {
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("project").request().get();
        assertThat(r.getStatus(), is(200));
    }

    @Test
    public void demandNotFounTest() {
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand/{id}").resolveTemplate("id", 1332).request().get();
        System.out.println(r.readEntity(String.class));
        assertThat(r.getStatus(), is(404));
    }
}