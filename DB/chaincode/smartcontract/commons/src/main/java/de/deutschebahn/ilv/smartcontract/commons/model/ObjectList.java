package de.deutschebahn.ilv.smartcontract.commons.model;

import de.deutschebahn.ilv.smartcontract.commons.DataConverter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
public class ObjectList<T> {
    private final List<T> objects;
    private final DataConverter<T> dataConverter;

    public ObjectList(List<T> objects, DataConverter<T> dataConverter) {
        this.objects = objects;
        this.dataConverter = dataConverter;
    }

    public ObjectList(JsonObject jsonObject, DataConverter<T> dataConverter) {
        this.dataConverter = dataConverter;
        objects = jsonObject.getJsonArray("objects")
                .stream()
                .map(ob -> (JsonObject) ob)
                .map((ob) -> dataConverter.deserialize(ob, DataConverter.DeserializeView.jsonFromFabricToObjectInApp))
                .collect(Collectors.toList());
    }

    public JsonObject toJson() {
        JsonArray jsonArray = objects.stream()
                .map(ob -> dataConverter.serialize(ob, DataConverter.SerializeView.objectInFabricToJsonToApp))
                .collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add)
                .build();

        return Json.createObjectBuilder().add("objects", jsonArray).build();
    }

    public List<T> getObjects() {
        return objects;
    }
}
