package de.deutschebahn.ilv;

import de.deutschebahn.ilv.businessobject.BOAction;
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
public class JsonArrayContainsAllStates extends BaseMatcher<JsonArray> {

    private final Collection<BOAction> expectedValues;
    private List<BOAction> givenButNotFoundValues = new ArrayList<>();
    private List<String> foundButButNotGivenValues = new ArrayList<>();
    private String noValidName = "";

    public JsonArrayContainsAllStates(BOAction... equalArg) {
        expectedValues = Arrays.asList(equalArg);
    }

    @Override
    public boolean matches(Object object) {

        JsonArray array = (JsonArray) object;
        if (array == null) {
            array = Json.createArrayBuilder().build();
        }

        JsonArray finalArray = array;

        givenButNotFoundValues = expectedValues.stream()
                .filter(value -> !TestUtils.JsonArrayContainsStringItem(finalArray, value.name()))
                .collect(Collectors.toList());

        for (int i = 0; i < array.size(); i++) {
            String name = array.getString(i);
            BOAction state = getStateFromString(name);
            if (state == null) {
                noValidName = "Invalid name found:" + name;
                return false;
            }

            if (!expectedValues.contains(state)) {
                foundButButNotGivenValues.add(state.name());
            }
        }

        return givenButNotFoundValues.isEmpty() && foundButButNotGivenValues.isEmpty();
    }

    private BOAction getStateFromString(String name) {
        try {
            return BOAction.valueOf(name);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("givenButNotFoundValues: ");
        description.appendValue(givenButNotFoundValues);
        description.appendText("foundButButNotGivenValues");
        description.appendValue(foundButButNotGivenValues);
    }
}
