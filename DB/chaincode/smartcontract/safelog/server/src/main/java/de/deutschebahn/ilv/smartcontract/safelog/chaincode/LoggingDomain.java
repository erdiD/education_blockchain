package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import org.hyperledger.fabric.shim.ledger.KeyValue;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoggingDomain {

    private final String domain;
    private final Map<String, Set<LogEntry>> logEntries;

    public LoggingDomain(String loggingDomain) {

        domain = loggingDomain;
        this.logEntries = new HashMap<>();
    }

    public void addNewEntry(String loggingKey, String loggingValue) {
        logEntries.computeIfAbsent(loggingKey, (key) -> new HashSet<>());
        logEntries.get(loggingKey).add(new LogEntry(loggingValue));
    }

    public void loadEntry(KeyValue loggingEntry) {
        logEntries
                .computeIfAbsent(loggingEntry.getKey(), (key) -> new HashSet<>())
                .add(LogEntry.fromJsonString(loggingEntry.getStringValue()));
    }

    public JsonObject toJson() {
        JsonObjectBuilder domainBuilder = Json.createObjectBuilder().add("domain", domain);
        JsonObjectBuilder entriesBuilder = Json.createObjectBuilder();

        for (Map.Entry<String, Set<LogEntry>> entries : logEntries.entrySet()) {
            JsonArrayBuilder logsArrayBuilder = Json.createArrayBuilder();
            for (LogEntry logEntry : entries.getValue()) {
                logsArrayBuilder.add(logEntry.toJson());
            }
            entriesBuilder.add(entries.getKey(), logsArrayBuilder);
        }

        domainBuilder.add("entries", entriesBuilder);
        return domainBuilder.build();
    }

    @Override
    public String toString() {
        return "LoggingDomain{" +
                "domain='" + domain + '\'' +
                ", logEntries=" + logEntries +
                '}';
    }
}
