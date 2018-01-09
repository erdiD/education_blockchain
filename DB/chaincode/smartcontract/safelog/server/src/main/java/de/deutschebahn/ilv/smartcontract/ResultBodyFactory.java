package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.smartcontract.safelog.chaincode.SafeLogParams;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by AlbertLacambraBasil on 26.07.2017.
 */
public class ResultBodyFactory {

    private static final String SUCCESSFUL_KEY = "successful";
    private static final String PARAMS_KEY = "params";
    private static final String ERROR_KEY = "error";

    public static JsonObject createSuccessInvocationResult(SafeLogParams safeLogParams) {
        return Json.createObjectBuilder()
                .add(SUCCESSFUL_KEY, true)
                .add(PARAMS_KEY, safeLogParams.toJson())
                .build();
    }

    public static JsonObject createFailedInvocationResult(SafeLogParams safeLogParams, String message) {
        return Json.createObjectBuilder()
                .add(SUCCESSFUL_KEY, false)
                .add(ERROR_KEY, message)
                .add(PARAMS_KEY, safeLogParams.toJson())
                .build();
    }
}
