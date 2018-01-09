package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 24.07.2017.
 */
public class LogEntry {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private String loggedText;
    private final LocalDateTime localDateTime;

    public LogEntry(String loggedText) {

        Objects.requireNonNull(loggedText);

        this.loggedText = loggedText;
        localDateTime = LocalDateTime.now();
    }

    private LogEntry(String loggedText, LocalDateTime localDateTime) {

        Objects.requireNonNull(localDateTime);

        this.loggedText = loggedText;
        this.localDateTime = localDateTime;
    }

    public String getLoggedText() {
        return loggedText;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("text", loggedText)
                .add("time", localDateTime.format(DATE_TIME_FORMATTER))
                .build();
    }

    public static LogEntry fromJsonString(String jsonObjectString) {

        Objects.requireNonNull(jsonObjectString);

        JsonObject jsonObject = Json.createReader(new StringReader(jsonObjectString)).readObject();
        String time = jsonObject.getString("time");
        TemporalAccessor temporalAccessor = DATE_TIME_FORMATTER.parse(time);
        return new LogEntry(jsonObject.getString("text"), LocalDateTime.from(temporalAccessor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (!loggedText.equals(logEntry.loggedText)) return false;
        return localDateTime.equals(logEntry.localDateTime);
    }

    @Override
    public int hashCode() {
        int result = loggedText.hashCode();
        result = 31 * result + localDateTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "logEntries='" + loggedText + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
