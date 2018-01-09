package de.deutschebahn.ilv.resource;

import de.deutschebahn.ilv.TestUtils;
import de.deutschebahn.ilv.businessobject.MarketRoleName;
import de.deutschebahn.ilv.flow.ILVScenarioIT;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Optional;

import static de.deutschebahn.ilv.TestUtils.getJsonEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by kschwartz on 17.08.17
 */
public class OfferResourcesIT extends ILVScenarioIT {

    @Test
    public void attachmentRoundTrip() throws IOException {
        String demandId = createDemand();
        String offerId = createOffer(demandId);

        // Send Attachment
        File file = new File("./src/test/resources/user.json");
        long originalFileSize = file.length();

        final FileDataBodyPart filePart = new FileDataBodyPart("attachment", file);
        final MultiPart multiPartEntity = new FormDataMultiPart().bodyPart(filePart);
        Response response = getWebTarget(MarketRoleName.OFFER_CREATOR).path("offer/{id}/attachment").resolveTemplate("id", offerId).request()
                .post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));

        assertThat(response.getStatus(), is(200));
        response = getWebTarget(MarketRoleName.OFFER_CREATOR).path("project/{id}").resolveTemplate("id", demandId).request().get();

        String responseAsString = response.readEntity(String.class);
        JsonObject responseAsJson = Json.createReader(new StringReader(responseAsString)).readObject();

        assertThat(response.getStatus(), is(200));
        assertThat(responseAsJson.getJsonArray("offers").isEmpty(), is(false));

        JsonObject entity = responseAsJson.getJsonArray("offers").getJsonObject(0);
        assertThat(entity.getJsonArray("attachments"), notNullValue());

        boolean foundAttachment = entity.getJsonArray("attachments").stream()
                .anyMatch(o -> ((JsonObject) o).containsKey("user.json"));
        assertThat(foundAttachment, is(true));

        Optional<JsonObject> fileObjectOptional = entity.getJsonArray("attachments").stream()
                .filter(o -> ((JsonObject) o).containsKey("user.json"))
                .findFirst().map(v -> (JsonObject) v);

        JsonObject foundJsonObject = fileObjectOptional.orElse(Json.createObjectBuilder().build());
        JsonObject fileInfoObject = foundJsonObject.getJsonArray("user.json").getJsonObject(0);
        String fileId = fileInfoObject.getString("fileId");

        // Validate Attachment
        response = getWebTarget(MarketRoleName.OFFER_CREATOR)
                .path("offer/{id}/attachment/{fileId}")
                .resolveTemplate("id", offerId)
                .resolveTemplate("fileId", fileId)
                .request()
                .get();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType(), is(MediaType.APPLICATION_OCTET_STREAM_TYPE));
        DataInputStream inputStream = new DataInputStream(response.readEntity(InputStream.class));
        assertThat((long) inputStream.available(), is(originalFileSize));
    }

    public String createDemand() {
        JsonObject demand = TestUtils.getJsonObjectFromFile("create-demand.json");
        Response r = getWebTarget(MarketRoleName.DEMAND_CREATOR).path("demand").request().post(Entity.json(demand));
        JsonObject entity = getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        return TestUtils.extractId(entity);
    }

    private String createOffer(String demandId) {
        JsonObjectBuilder offerBuilder = TestUtils.getJsonObjectBuilderFromFile("create-offer.json");
        offerBuilder.add("demandId", demandId);
        Response r = getWebTarget(MarketRoleName.OFFER_CREATOR).path("offer").request().post(Entity.json(offerBuilder.build()));
        JsonObject entity = getJsonEntity(r);
        assertThat(entity.toString(), r.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        return TestUtils.extractId(entity);
    }


}