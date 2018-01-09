package de.deutschebahn.ilv;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import javax.json.Json;
import javax.json.JsonArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 11.06.2017.
 */
public class JsonArrayContainsStringItems extends BaseMatcher<JsonArray> {

    private final boolean matchAll;
    private final Collection<String> expectedValues;
    private List<String> notMatchedValues = new ArrayList<>();

    public JsonArrayContainsStringItems(String... equalArg) {
        expectedValues = Arrays.asList(equalArg);
        matchAll = false;
    }

    public JsonArrayContainsStringItems(boolean matchAll, String... equalArg) {
        this.matchAll = matchAll;
        expectedValues = Arrays.asList(equalArg);
    }

    @Override
    public boolean matches(Object object) {

        JsonArray array = (JsonArray) object;
        if (array == null) {
            array = Json.createArrayBuilder().build();
        }

        JsonArray finalArray = array;

        if (matchAll && expectedValues.size() != array.size()) {
            return false;
        }

        notMatchedValues = expectedValues.stream()
                .filter(value -> !TestUtils.JsonArrayContainsStringItem(finalArray, value))
                .collect(Collectors.toList());

        return notMatchedValues.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Given values: ");
        description.appendValue(expectedValues);
        description.appendText("Following values where not found");
        description.appendValue(notMatchedValues);
    }
}
