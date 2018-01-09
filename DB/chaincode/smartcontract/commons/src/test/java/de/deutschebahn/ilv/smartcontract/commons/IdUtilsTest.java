package de.deutschebahn.ilv.smartcontract.commons;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
public class IdUtilsTest {
    @Test
    public void extractProjectId() throws Exception {

        String pid = "P_" + UUID.randomUUID().toString();
        String extractPid = IdUtils.extractProjectId(pid);

        assertThat(extractPid, is(pid));

        pid = "P_" + UUID.randomUUID().toString();
        String objectId = pid  + IdUtils.EXT_SEPARATOR + "C" + IdUtils.EXT_SEPARATOR;
        extractPid = IdUtils.extractProjectId(objectId);

        assertThat(extractPid, is(pid));
    }
}