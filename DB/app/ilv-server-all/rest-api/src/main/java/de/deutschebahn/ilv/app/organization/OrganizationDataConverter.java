package de.deutschebahn.ilv.app.organization;

import de.deutschebahn.ilv.domain.Organization;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by alacambra on 04.06.17.
 */
public class OrganizationDataConverter {
    public Organization deserialize(JsonObject jsonObject) {
        throw new UnsupportedOperationException("No deserialization implemented for Organization");
    }

    public JsonObject serialize(Organization organization) {
        return Json.createObjectBuilder()
                .add("id", organization.getId())
                .add("name", organization.getName())
                .build();
    }
}
