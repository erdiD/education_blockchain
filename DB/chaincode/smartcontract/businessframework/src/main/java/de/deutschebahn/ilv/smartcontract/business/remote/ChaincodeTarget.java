package de.deutschebahn.ilv.smartcontract.business.remote;

import de.deutschebahn.ilv.smartcontract.commons.ChaincodeResponseMessage;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;

import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
public class ChaincodeTarget {

    private static final Logger logger = Logger.getLogger(ChaincodeTarget.class.getName());
    private String chaincodeName;

    public ChaincodeTarget(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public ChaincodeInvocation withChaincodeStub(ChaincodeStub chaincodeStub) {
        return new ChaincodeInvocation(chaincodeStub, chaincodeName);
    }

    public static class ChaincodeInvocation {

        private final ChaincodeStub chaincodeStub;
        private String target;
        private ChaincodeInvocationMessage.Type type;
        private String userId;
        private String function;
        private List<String> params;

        public ChaincodeInvocation(ChaincodeStub chaincodeStub, String target) {
            this.chaincodeStub = chaincodeStub;
            this.target = target;
        }

        public ChaincodeInvocation asPeer() {
            if (type != null) {
                throw new RuntimeException("Type already set");
            }
            type = ChaincodeInvocationMessage.Type.PEER;
            return this;
        }

        public ChaincodeInvocation asUser(String userId) {
            if (type != null) {
                throw new RuntimeException("Type already set");
            }

            this.userId = userId;
            type = ChaincodeInvocationMessage.Type.USER;
            return this;
        }

        public ChaincodeInvocation function(String method) {
            this.function = method;
            return this;
        }

        public ChaincodeInvocation params(List<String> params) {
            this.params = params;
            return this;
        }

        public ChaincodeInvocation params(String... params) {
            this.params = Arrays.asList(params);
            return this;
        }

        public ChaincodeExecutor build() {

            if (type == null) {
                throw new RuntimeException("Type not given");
            }

            if (params == null) {
                params = Collections.emptyList();
            } else if (params.contains("peer")) {
                throw new RuntimeException();
            }
            return new ChaincodeExecutor(chaincodeStub, target, new ChaincodeInvocationMessage(function, userId, params, type));
        }
    }

    public static class ChaincodeExecutor {

        private final ChaincodeStub chaincodeStub;
        private final String target;
        private final ChaincodeInvocationMessage chaincodeMessageInvocation;

        public ChaincodeExecutor(ChaincodeStub chaincodeStub, String target, ChaincodeInvocationMessage chaincodeMessageInvocation) {
            this.chaincodeStub = chaincodeStub;
            this.target = target;
            this.chaincodeMessageInvocation = chaincodeMessageInvocation;
        }

        public Chaincode.Response execute() {
            return chaincodeStub.invokeChaincode(
                    target,
                    chaincodeMessageInvocation.getSendParams()
            );
        }

        public <T> Optional<T> execute(Function<JsonObject, T> resultReader) {
            Chaincode.Response response = execute();

            if (response.getStatus() != Chaincode.Response.Status.SUCCESS) {
                byte[] bytes = response.getPayload();
                if (bytes == null) {
                    bytes = new byte[0];
                }
                logger.warning("[execute] " + new String(bytes));
                return Optional.empty();
            } else if (response.getPayload() == null || response.getPayload().length == 0) {
                logger.warning("[execute] Execution returns success but no payload has been found. Response message="
                        + response.getMessage()
                        + ". Invocation=" + target + ", messageInvocation=" + chaincodeMessageInvocation);

                return Optional.empty();
            }

            ChaincodeResponseMessage chaincodeResponseMessage = new ChaincodeResponseMessage(response.getPayload());
            chaincodeResponseMessage.setMessageId(chaincodeMessageInvocation.getMessageId());
            if (chaincodeResponseMessage.getStatus().isSuccessful()) {
                return Optional.of(resultReader.apply(chaincodeResponseMessage.getPayload()));
            } else {
                logger.info("[execute] " + chaincodeResponseMessage);
                return Optional.empty();
            }
        }
    }
}

