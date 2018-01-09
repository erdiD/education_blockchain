package de.deutschebahn.ilv.smartcontract.safelog.chaincode;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by AlbertLacambraBasil on 28.07.2017.
 */
public class LoggingDomainTest {

    LoggingDomain cut;

    @Test
    public void toJson() throws Exception {

        cut = new LoggingDomain("ILV");

        JsonObject jsonObject = Json
                .createReader(getClass().getClassLoader().getResourceAsStream("domain-query.json"))
                .readObject();

        JsonObject entriesObject = jsonObject.getJsonObject("entries");

        for (String key : entriesObject.keySet()) {
            JsonArray entries = entriesObject.getJsonArray(key);
            for (int i = 0; i < entries.size(); i++) {
                KeyValue keyValue = mock(KeyValue.class);
                when(keyValue.getKey()).thenReturn(key);
                when(keyValue.getStringValue()).thenReturn(entries.getJsonObject(i).toString());
                cut.loadEntry(keyValue);
            }
        }

        assertThat(cut.toJson().toString(), is(jsonObject.toString()));
    }

}