package de.deutschebahn.ilv.test.client;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by AlbertLacambraBasil on 29.07.2017.
 */
public class ContractClient extends ProjectClient {

    private String contractId;

    @Override
    protected String getObjectKey() {
        return "contract";
    }

    @Override
    protected boolean checkHistory() {
        return false;
    }

    @Override
    protected JsonObject getAndValidateObjectExistenceOnProjectEntity(JsonValue jsonValue) {
        assertThat(jsonValue, not(nullValue()));
        return (JsonObject) jsonValue;
    }

    @Override
    protected String getEntityId() {
        return contractId;
    }

    @Override
    public String getObjectResourcePath() {
        return "contract";
    }

    @Override
    protected String assignObjectId(String id) {
        return contractId = id;
    }

    @Override
    protected JsonObject loadJsonFormFile() {
        throw new UnsupportedOperationException("Contract are dynamically created");
    }

    public void loadContractId(WebTarget webTarget) {
        JsonObject jsonObject = get(webTarget, Response.Status.OK);
        contractId = jsonObject.getString("id");
    }
}
