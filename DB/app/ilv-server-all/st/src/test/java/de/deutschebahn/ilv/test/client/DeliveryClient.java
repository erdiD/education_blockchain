package de.deutschebahn.ilv.test.client;

import de.deutschebahn.ilv.TestUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by AlbertLacambraBasil on 16.08.2017.
 */
public class DeliveryClient extends ProjectClient {

    private String deliveryId;

    public void addPspAndValidate(WebTarget webTarget, Response.Status expectedStatus, String... psps) {

        if (psps == null) {
            psps = new String[0];
        }
        String pspAll = Stream.of(psps).collect(Collectors.joining(","));

        Response response = webTarget.path(getObjectResourcePath())
                .path(String.valueOf(deliveryId))
                .queryParam("psps", pspAll)
                .request()
                .put(Entity.json(Json.createObjectBuilder().build()));

        JsonObject entity = TestUtils.getJsonEntity(response);

        assertThat(entity.toString(), response.getStatus(), is(expectedStatus.getStatusCode()));
    }

    public void uploadDeliveryAndValidate(WebTarget webTarget,
                                          Response.Status expectedStatus,
                                          Integer expectedCorrectDeliveryEntriesNumber,
                                          Integer expectedErrorDeliveryEntriesNumber,
                                          String fileName,
                                          String... expectedPsps) {
        Response response = TestUtils.uploadFile(
                webTarget.path(getObjectResourcePath()).path(String.valueOf(deliveryId)), fileName, "attachment");
        assertThat(response.getStatus(), is(expectedStatus.getStatusCode()));

        if (expectedStatus.getFamily() != Response.Status.Family.SUCCESSFUL) {
            return;
        }

        JsonObject jsonObject = TestUtils.getJsonEntity(response);
        assertThat(jsonObject.getString("id"), is(deliveryId));
        assertThat(jsonObject.containsKey("errors"), is(true));
        assertThat(jsonObject.containsKey("success"), is(true));

        JsonArray errorsArray = jsonObject.getJsonArray("errors");
        JsonArray successArray = jsonObject.getJsonArray("success");

        if (expectedErrorDeliveryEntriesNumber != null) {
            assertThat(errorsArray.toString() + " // " + successArray.toString(), errorsArray.size(),
                    is(expectedErrorDeliveryEntriesNumber));
        }

        if (expectedCorrectDeliveryEntriesNumber != null) {
            assertThat(errorsArray.toString() + " // " + successArray.toString(), successArray.size(),
                    is(expectedCorrectDeliveryEntriesNumber));


            List<String> psps = expectedPsps.length > 0 ? Arrays.asList(expectedPsps) : Collections.emptyList();

            for (int i = 0; i < successArray.size(); i++) {
                String pspId = successArray.getJsonObject(i).getString("pspId");
                if (psps.contains(pspId)) {
                    psps.remove(pspId);
                }
            }

            assertThat(psps.isEmpty(), is(true));
        }
    }

    public void uploadDeliveryAndValidate(WebTarget webTarget,
                                          List<String> lines,
                                          Integer expectedCorrectDeliveryEntriesNumber,
                                          Integer expectedErrorDeliveryEntriesNumber,
                                          String... expectedPsps) throws IOException {

        String fileName = UUID.randomUUID().toString();
        Path path = Paths.get("./" + fileName);
        Files.copy(new ByteArrayInputStream(lines.stream().collect(Collectors.joining("\n")).getBytes("UTF-8")), path);
        File file = path.toFile();
        uploadDeliveryAndValidate(webTarget, Response.Status.OK, expectedCorrectDeliveryEntriesNumber,
                expectedErrorDeliveryEntriesNumber,
                file.getAbsolutePath());

        Files.delete(Paths.get(file.toURI()));
    }

    @Override
    protected String getObjectKey() {
        return "delivery";
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
        return deliveryId;
    }

    @Override
    public String getObjectResourcePath() {
        return "delivery";
    }

    @Override
    protected String assignObjectId(String id) {
        throw new UnsupportedOperationException("Not required since deliveries are automatically created");
    }

    @Override
    protected JsonObject loadJsonFormFile() {
        throw new UnsupportedOperationException("Not required since deliveries are automatically created");
    }

    public void loadDeliveryId(WebTarget webTarget) {
        JsonObject jsonObject = get(webTarget, Response.Status.OK);
        deliveryId = jsonObject.getString("id");
    }
}
