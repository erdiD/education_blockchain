package de.deutschebahn.ilv.app.user;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.User;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDataConverter {
    public User deserialize(JsonObject jsonObject) {
        throw new UnsupportedOperationException("No deserialization implemented for User");
    }

    public JsonObject serialize(User user) {

        if (user == null) {
            return Json.createObjectBuilder().build();
        }

        JsonArrayBuilder roleArrayBuilder = Json.createArrayBuilder();

        Set<String> roleSet = user.getMarketRole().stream()
                .map(MarketRoleName::toString)
                .collect(Collectors.toSet());

        roleSet.forEach(roleArrayBuilder::add);

		boolean userIsDemandCreator = user.getMarketRole().stream().anyMatch(MarketRoleName.DEMAND_CREATOR::equals);

        return Json.createObjectBuilder()
                .add("id", user.getId())
                .add("firstName", user.getFirsName())
                .add("username", user.getUserName())
                .add("lastName", user.getLastName())
                .add("name", user.getLastName())
                .add("canCreateDemand", userIsDemandCreator)
				.add("avatarImageSrc", "assets/img/userBlank.jpg")
				.build();
    }
}