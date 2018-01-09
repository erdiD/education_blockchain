package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LogEntryTest {

    LogEntry cut;

    @Test
    public void toJson() throws Exception {

        JsonObject jsonObject = Json
                .createReader(getClass().getClassLoader().getResourceAsStream("entry-query.json"))
                .readObject();

        String entryString = jsonObject
                .getJsonArray("ILV\u0000ObjectD1\u0000")
                .getJsonObject(0)
                .toString();

        cut = LogEntry.fromJsonString(entryString);
        assertThat(cut.toJson().toString(), is(entryString));
    }
}