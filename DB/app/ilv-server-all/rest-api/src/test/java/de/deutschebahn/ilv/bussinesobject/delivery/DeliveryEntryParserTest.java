package de.deutschebahn.ilv.bussinesobject.delivery;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DeliveryEntryParserTest {

    DeliveryEntryParser cut;

    @Before
    public void setUp() {
        cut = new DeliveryEntryParser();
        cut.logger = Logger.getLogger(DeliveryEntryParser.class.getName());
    }

    @Test
    public void parseHasAllErrors() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("delivery.txt");
        DeliveryParserResult deliveryParserResult = cut.parse(Arrays.asList("", ""), inputStream, true);
        assertThat(deliveryParserResult.hasErrors(), is(true));
        assertThat(deliveryParserResult.getErrors().size(), is(10));
        assertThat(deliveryParserResult.getDeliveryEntries().size(), is(0));

    }

    @Test
    public void parseHasHalfErrors() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("delivery.txt");
        DeliveryParserResult deliveryParserResult = cut.parse(Arrays.asList("pspId12"), inputStream, true);
        assertThat(deliveryParserResult.toString(), deliveryParserResult.hasErrors(), is(true));
        assertThat(deliveryParserResult.toString(), deliveryParserResult.getErrors().size(), is(5));
        assertThat(deliveryParserResult.toString(), deliveryParserResult.getDeliveryEntries().size(), is(5));

    }

    @Test
    public void parseHasNoErrors() throws Exception {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("delivery.txt");
        DeliveryParserResult deliveryParserResult = cut.parse(Arrays.asList("pspId12", "pspId120"), inputStream, true);
        assertThat(deliveryParserResult.toString(), deliveryParserResult.hasErrors(), is(false));
        assertThat(deliveryParserResult.toString(), deliveryParserResult.getErrors().size(), is(0));
        assertThat(deliveryParserResult.toString(), deliveryParserResult.getDeliveryEntries().size(), is(10));

    }

}