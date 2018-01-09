package de.deutschebahn.ilv.test.client;


import de.deutschebahn.ilv.TestUtils;
import org.hamcrest.CoreMatchers;

import javax.json.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by AlbertLacambraBasil on 16.06.2017.
 */
public class OfferClient extends ProjectClient {

    private String offerId;
    private String filename = "create-offer.json";

    public OfferClient() {
    }

    @Override
    protected JsonObject loadJsonFormFile() {
        JsonObjectBuilder objectBuilder = TestUtils.getJsonObjectBuilderFromFile(filename);
        objectBuilder.add("demandId", getDemandId());
        return objectBuilder.build();
    }

    public void verifyUpdate(WebTarget webTarget, Response.Status expectedStatus) {
        JsonObject offer = TestUtils.getJsonObjectFromFile("create-offer.json");
        Response response = webTarget.path(getObjectResourcePath()).path(String.valueOf(getEntityId())).request().put(Entity.json(offer));
        assertThat(response.getStatus(), is(expectedStatus.getStatusCode()));
    }

    public void validateNoOffersReceived(WebTarget webTarget) {

        assertThat(get(webTarget, Response.Status.OK).keySet().size(), CoreMatchers.is(0));
    }

    @Override
    protected String getObjectKey() {
        return "offers";
    }

    @Override
    protected boolean checkHistory() {
        return true;
    }

    @Override
    protected JsonObject getAndValidateObjectExistenceOnProjectEntity(JsonValue jsonValue) {
        JsonArray jsonArray = (JsonArray) jsonValue;
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            if (offerId.equals(jsonObject.getString("id"))) {
                return jsonObject;
            }
        }
        return Json.createObjectBuilder().build();
    }

    @Override
    protected String getEntityId() {
        return offerId;
    }

    @Override
    public String getObjectResourcePath() {
        return "offer";
    }

    @Override
    protected String assignObjectId(String id) {
        return offerId = id;
    }


    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
