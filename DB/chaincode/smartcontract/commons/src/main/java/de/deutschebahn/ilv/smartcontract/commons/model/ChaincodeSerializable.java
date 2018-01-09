package de.deutschebahn.ilv.smartcontract.commons.model;

import javax.json.JsonObject;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 03.11.2017.
 */
public interface ChaincodeSerializable {

    List<String> toParams();
    void fromParams(List<String> params);
    JsonObject toJsonObject();
    void fromJsonObject();

}
