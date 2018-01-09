package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.domain.User;

import javax.json.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserDataConverter implements DataConverter<User> {

    public UserDataConverter() {
    }

    public User deserialize(JsonObject jsonObject, DeserializeView view) {
        User user = new User();
        user.setUserName(SerializationHelper.getValueOrException("username", jsonObject::getString));
        user.setFirsName(SerializationHelper.getValueOrException("firstName", jsonObject::getString));
        user.setLastName(SerializationHelper.getValueOrException("lastName", jsonObject::getString));
        user.setOrganizationId(SerializationHelper.getValueOrException("organizationId", jsonObject::getString));
        List<String> marketRoles = SerializationHelper.getValueOrException("marketRoles", jsonObject::getJsonArray, SerializationHelper::deserializeToArray);
        user.setMarketRole(marketRoles.stream().map(MarketRoleName::valueOf).collect(Collectors.toList()));

        switch (view) {
            case newObjectCreationFromJson:
                break;
            case objectBetweenChaincodes:
            case updateObjectFromJson:
            case jsonInDatabaseToObjectInFabric:
            case jsonFromFabricToObjectInApp:
                user.setId(SerializationHelper.getValueOrException("id", jsonObject::getString));
                break;
        }
        return user;
    }

    @Override
    public String getAssignedType() {
        return User.class.getSimpleName();
    }

    public JsonObject serialize(User user, SerializeView view) {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        SerializationHelper.serializeValueOrException(builder::add, "firstName", user.getFirsName());
        SerializationHelper.serializeValueOrException(builder::add, "username", user.getUserName());
        SerializationHelper.serializeValueOrException(builder::add, "lastName", user.getLastName());
        SerializationHelper.serializeValueOrException(builder::add, "organizationId", user.getOrganizationId());
        SerializationHelper.serializeValueOrException(builder::add, "avatarImageSrc", "assets/img/userBlank.jpg");

        JsonArray roles = user.getMarketRole().stream().map(Enum::name).collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
        SerializationHelper.serializeValueOrException(builder::add, "marketRoles", roles);

        switch (view) {
            case createJsonForNewObject:
                break;
            case objectInAppToJsonToFabric:
            case objectInFabricToJsonInDatabase:
            case objectInFabricToJsonToApp:
            case objectBetweenChaincodes:
                boolean canCreateDemand = user.getMarketRole().contains(MarketRoleName.DEMAND_CREATOR);
                SerializationHelper.serializeValueOrException(builder::add, "canCreateDemand", canCreateDemand);
                SerializationHelper.serializeValueOrException(builder::add, "id", user.getId());
                break;
        }

        return builder.build();
    }
}