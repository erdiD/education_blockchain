package de.deutschebahn.ilv.smartcontract.business;

import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 01.09.2017.
 */
public class IdUtilsTest {
    CompositeKey demandId;
    CompositeKey offerId;
    String uuid;

    @Before
    public void setUp() {
        demandId = IdUtils.generateDemandKey();
        uuid = demandId.getObjectType();
        offerId = IdUtils.generateOfferKey(demandId, 2);
    }

    @Test
    public void generateDemandId() throws Exception {
        CompositeKey compositeKey = IdUtils.generateDemandKey();
        assertThat(compositeKey.getAttributes().get(0), is("D"));
        assertThat(compositeKey.getObjectType(), notNullValue());
    }

    @Test
    public void generateOfferId() throws Exception {
        validate(IdUtils.generateOfferKey(demandId, 2), uuid, "O", "2");
    }

    @Test
    public void generateContractId() throws Exception {
        validate(IdUtils.generateContractKey(offerId), uuid, "O", "2", "C");
    }

    @Test
    public void generateDeliveryId() throws Exception {
        validate(IdUtils.generateDeliveryKey(offerId), uuid, "O", "2", "L");
    }

    @Test
    public void regenerateOfferId() throws Exception {
        String projectId = IdUtils.createProjectId();
        String allOffersId = IdUtils.recreateOfferId(projectId);
        assertThat(projectId + "\u0000O\u0000", is(allOffersId));
    }

    @Test
    public void generateDeliveryEntryId() throws Exception {
        validate(IdUtils.generateDeliveryEntryKey(offerId, 5), uuid, "O", "2", "I", "5");
    }

    @Test
    public void generatePaymentId() throws Exception {
        validate(IdUtils.generatePaymentKey(offerId, 15), uuid, "O", "2", "Y_15");
        System.out.println(IdUtils.generatePaymentKey(offerId, 15));
    }

    @Test
    public void stringToCompositeKey() {
        CompositeKey compositeKey = IdUtils.stringToCompositeKey(offerId.toString());
        validate(compositeKey, uuid, "O", "2");
    }

    @Test
    public void getProjectKey() {
        String key = IdUtils.getProjectId(demandId);
        String expected = demandId.toString().substring(0, demandId.toString().length() - 3);
        assertThat(key, is(expected));
    }

    private void validate(CompositeKey compositeKey, String objectType, String... expectedAttributes) {
        System.out.println(compositeKey.toString());
        assertThat(compositeKey.getObjectType(), is(objectType));
        List<String> actualAttributes = compositeKey.getAttributes();
        assertThat(actualAttributes + " || " + Arrays.asList(expectedAttributes),
                actualAttributes.size(), is(expectedAttributes.length));
    }

}
