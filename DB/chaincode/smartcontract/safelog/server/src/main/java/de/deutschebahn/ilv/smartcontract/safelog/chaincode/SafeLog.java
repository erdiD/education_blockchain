/*
Copyright DTCC, IBM 2016, 2017 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import de.deutschebahn.ilv.smartcontract.ResultBodyFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import javax.json.*;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;


public class SafeLog extends ChaincodeBase {

    private final static KeyValue DEFAULT_KEY_VALUE = new KeyValue() {
        @Override
        public String getKey() {
            return "";
        }

        @Override
        public byte[] getValue() {
            return new byte[0];
        }

        @Override
        public String getStringValue() {
            return "";
        }
    };

    private enum Action {
        save, delete, queryByDomain, queryByLogKey
    }

    private static final List<String> AVAILABLE_ACTIONS =
            Arrays.stream(Action.values()).map(Action::name).collect(Collectors.toList());

    private static Logger logger = Logger.getLogger(SafeLog.class.getName());

    public static void main(String[] args) throws Exception {
        new SafeLog().start(args);
    }

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            final String function = stub.getFunction();
            switch (function) {
                case "init":
                    return init(stub, stub.getParameters().stream().toArray(String[]::new));
                default:
                    return newErrorResponse(format("Unknown function: %s", function));
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {

        String fn = stub.getFunction();
        if (!AVAILABLE_ACTIONS.contains(fn)) {
            return newErrorResponse(format("Unknown function: %s. Available actions are %s", fn, AVAILABLE_ACTIONS));
        }

        Action action = Action.valueOf(fn);
        List<String> args = stub.getParameters();
        if (args == null) {
            args = Collections.emptyList();
        }

        try {
            switch (action) {
                case save:
                    return save(stub, args);
                case delete:
                    return delete(stub, args);
                case queryByDomain:
                    return queryByDomain(stub, args);
                case queryByLogKey:
                    return queryByLogKey(stub, args);
                default:
                    return newErrorResponse("That should never happens");
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            String message = e.getMessage();
            if (e.getMessage() == null) {
                message = e.getClass().getCanonicalName() + ". No more information available";
            }

            return newErrorResponse(createFailedInvocationResult(message).toString());
        }
    }

    //within Init we may create a new LoggingDomain
    private Response init(ChaincodeStub stub, String[] args) {

        logger.info("Init Starts");

        if (args.length == 0) {
            return newSuccessResponse();
        }

        return newErrorResponse();

    }

    private Response save(ChaincodeStub stub, List<String> args) {

        SafeLogParams safeLogParams = SafeLogParams
                .forNewEntry(args)
                .orElseThrow(illegalArgumentExceptionSupplier(args));

        JsonArray state = getKeyState(stub, safeLogParams);

        JsonArrayBuilder builder;
        if (state != null) {
            builder = createBuilder(state);
        } else {
            builder = Json.createArrayBuilder();
        }
        builder.add(new LogEntry(safeLogParams.getLogText()).toJson());

        CompositeKey compositeKey = stub.createCompositeKey(safeLogParams.getDomain(), safeLogParams.getLogKey());
        JsonObject responseBody;
        Response response;

        stub.putStringState(
                compositeKey.toString(),
                builder.build().toString()
        );

        responseBody = ResultBodyFactory.createSuccessInvocationResult(safeLogParams);
        response = newSuccessResponse(responseBody.toString());
        return response;
    }

    public Response queryByDomain(ChaincodeStub stub, List<String> args) {

        SafeLogParams safeLogParams = SafeLogParams
                .forDomainQuery(args)
                .orElseThrow(illegalArgumentExceptionSupplier(args));

        final QueryResultsIterator<KeyValue> keyValues = stub.getStateByPartialCompositeKey(safeLogParams.getDomain());
        LoggingDomain safeLog = new LoggingDomain(safeLogParams.getDomain());

        Iterator<KeyValue> keyValueIterator = keyValues.iterator();
        KeyValue keyValue = DEFAULT_KEY_VALUE;
        while (keyValueIterator.hasNext()) {
            try {
                keyValue = keyValueIterator.next();
                safeLog.loadEntry(keyValue);
            } catch (NoSuchElementException e) {
                logger.info("[queryByDomain] NoSuchElementException happened. Last key=" + keyValue.getKey());
            }
        }

        String responseBody = safeLog.toJson().toString();
        return newSuccessResponse(responseBody.getBytes(StandardCharsets.UTF_8));
    }

    public Response queryByLogKey(ChaincodeStub stub, List<String> args) {

        SafeLogParams safeLogParams = SafeLogParams
                .forLogKey(args)
                .orElseThrow(illegalArgumentExceptionSupplier(args));

        JsonArray logs = getKeyState(stub, safeLogParams);

        if (logs == null) {
            return newErrorResponse(
                    ResultBodyFactory.createFailedInvocationResult(safeLogParams, "key has no contents").toString()
            );
        }

        logger.info("[queryByLogKey] sending response body:" + logs);
        return newSuccessResponse(logs.toString().getBytes(StandardCharsets.UTF_8));
    }

    private JsonArray getKeyState(ChaincodeStub stub, SafeLogParams safeLogParams) {
        CompositeKey compositeKey = new CompositeKey(safeLogParams.getDomain(), safeLogParams.getLogKey());
        String state = stub.getStringState(compositeKey.toString());

        if (state == null || state.isEmpty()) {
            return null;
        }

        return Json.createReader(new StringReader(state)).readArray();
    }


    private static JsonArrayBuilder createBuilder(JsonArray jsonArray) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (JsonValue value : jsonArray) {
            builder.add(value);
        }

        return builder;
    }

    private static Supplier<IllegalArgumentException> illegalArgumentExceptionSupplier(List<String> args) {
        return () -> new IllegalArgumentException("not possible to load params. Args=" + Arrays.asList(args));
    }


    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            throw new IllegalArgumentException("Incorrect number of arguments. Expecting: delete(LoggingDomain)");

        final String LoggingSpaceName = args.get(0);

        stub.delState(LoggingSpaceName);

        return newSuccessResponse();
    }

    private JsonObject createFailedInvocationResult(String message) {

        if (message == null) {
            message = "";
        }

        return Json.createObjectBuilder()
                .add("successful", false)
                .add("error", message)
                .build();
    }
}
