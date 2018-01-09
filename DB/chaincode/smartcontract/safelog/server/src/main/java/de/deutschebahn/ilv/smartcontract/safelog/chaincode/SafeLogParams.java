package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Optional;

/**
 * Created by AlbertLacambraBasil on 24.07.2017.
 */
public class SafeLogParams {

    private String domain;
    private String logKey;
    private String logText;

    public SafeLogParams(String domain, String logKey, String logText) {
        this.domain = domain;
        this.logKey = logKey;
        this.logText = logText;
    }

    public static Optional<SafeLogParams> forDomainQuery(List<String> args) {
        if (isValidArguments(args, 1)) {
            String domain = args.get(0);
            return Optional.of(new SafeLogParams(domain, null, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<SafeLogParams> forLogKey(List<String> args) {
        if (isValidArguments(args, 2)) {
            String domain = args.get(0);
            String key = args.get(1);
            return Optional.of(new SafeLogParams(domain, key, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<SafeLogParams> forNewEntry(List<String> args) {
        if (isValidArguments(args, 3)) {
            String domain = args.get(0);
            String key = args.get(1);
            String logText = args.get(2);
            return Optional.of(new SafeLogParams(domain, key, logText));
        } else {
            return Optional.empty();
        }
    }

    private static boolean isValidArguments(List<String> args, int expectedNumberOfArguments) {
        return (args != null && args.size() == expectedNumberOfArguments);
    }

    public String getDomain() {
        return domain;
    }

    public String getLogKey() {
        return logKey;
    }

    public String getLogText() {
        return logText;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("domain", domain)
                .add("logKey", logKey);

        if (logText != null) {
            builder.add("logText", logText).build();
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "SafeLogParams{" +
                "domain='" + domain + '\'' +
                ", logKey='" + logKey + '\'' +
                ", logText='" + logText + '\'' +
                '}';
    }
}
