package de.deutschebahn.ilv.smartcontract.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by AlbertLacambraBasil on 12.10.2017.
 */
public class ChaincodeInvocationMessage {

    private static final Logger logger = Logger.getLogger(ChaincodeInvocationMessage.class.getName());

    public enum Type {
        PEER, USER;
    }

    private String function;

    private String principalId;
    private List<String> params;
    private Type type;
    private long mid;

    /**
     * Creates ChaincodeInvocationMessage to be sent
     *
     * @param function
     * @param principalId
     * @param params
     * @param type
     */
    public ChaincodeInvocationMessage(String function, String principalId, List<String> params, Type type) {
        this.function = function;
        this.principalId = principalId;

        this.params = params;
        if (this.params == null) {
            this.params = new ArrayList<>(3);
        }
        if ("peer".equalsIgnoreCase(principalId) || params.contains("peer")) {
            throw new RuntimeException();
        }
        this.type = type;
        mid = System.currentTimeMillis();
        logger.info("[ChaincodeInvocationMessage] Message created:" + this);

    }

    /**
     * Creates ChaincodeInvocationMessage from an incoming message. It is used this way to avoid ChaincodeStub dependencies,
     * so that client classes can still use it
     *
     * @param function
     * @param params
     */
    public ChaincodeInvocationMessage(String function, List<String> params) {
        this.function = function;
        mid = Long.parseLong(params.get(0));
        type = Type.valueOf(params.get(1));
        int startIndex = 2;
        if (type == Type.USER) {
            principalId = params.get(2);
            startIndex = 3;
        }

        if (startIndex < params.size()) {
            this.params = params.subList(startIndex, params.size());
        } else {
            this.params = Collections.emptyList();
        }
    }

    /**
     * Used for interchaincode communication. Params should include function.
     *
     * @return
     */
    public List<byte[]> getSendParams() {
        List<String> sendParams = new ArrayList<>(Arrays.asList(getSendParamsAsStringArray()));
        sendParams.add(0, function);
        return sendParams.stream().map(String::getBytes).collect(Collectors.toList());
    }

    /**
     * Used for client - chaincode communication. Params should NOT include function
     * TODO: review if it can be standardized with getSendParams
     * TODO: search appropriate name
     *
     * @return
     */
    public String[] getSendParamsAsStringArray() {
        List<String> sendParams = new ArrayList<>();
        sendParams.add(String.valueOf(mid));
        sendParams.add(type.name());
        if (type == Type.USER) {
            sendParams.add(principalId);
        }
        sendParams.addAll(params);
        return sendParams.toArray(new String[0]);
    }


    public String getFunction() {
        return function;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public List<String> getParams() {
        return params;
    }

    public Type getType() {
        return type;
    }

    public long getMessageId() {
        return mid;
    }

    @Override
    public String toString() {
        return "ChaincodeInvocationMessage{" +
                "mid=" + mid +
                ", function='" + function + '\'' +
                ", principalId='" + principalId + '\'' +
                ", params=" + params +
                ", type=" + type +
                '}';
    }
}
