package de.deutschebahn.ilv.smartcontract.commons;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 07.07.2017.
 */
public class SerializationHelperTest {

    @Test
    public void convertToBigDecimal() throws Exception {

        verifyBigDecimalConversion("1.000.100,02", new BigDecimal("1000100.02"), "1.000.100,02");
        verifyBigDecimalConversion("1.000.100,002", new BigDecimal("1000100.002"), "1.000.100,00");
        verifyBigDecimalConversion("100,102", new BigDecimal("100.102"), "100,10");
        verifyBigDecimalConversion("00,002", new BigDecimal("0.002"), "0,00");
        verifyBigDecimalConversion("0", BigDecimal.ZERO, "0,00");
        verifyBigDecimalConversion("10000,455", new BigDecimal("10000.455"), "10.000,46");

    }

    private void verifyBigDecimalConversion(String strValue, BigDecimal rawBigDecimal, String expectedFormattedValue) {
        BigDecimal value = SerializationHelper.convertToBigDecimal(strValue);
        assertThat(value, is(rawBigDecimal));
        assertThat(SerializationHelper.serializeBigDecimalCurrency(value), is(expectedFormattedValue));
    }

    @Test
    public void convertToDate() throws Exception {
        String value = SerializationHelper.serializeBigDecimalCurrency(new BigDecimal(1000000.06));
        assertThat(value, is("1.000.000,06"));

        value = SerializationHelper.serializeBigDecimalCurrency(new BigDecimal(1000000.006));
        assertThat(value, is("1.000.000,01"));

        value = SerializationHelper.serializeBigDecimalCurrency(new BigDecimal(1000000000.006));
        assertThat(value, is("1.000.000.000,01"));

        value = SerializationHelper.serializeBigDecimalCurrency(new BigDecimal(1));
        assertThat(value, is("1,00"));

        value = SerializationHelper.serializeBigDecimalCurrency(new BigDecimal(1.5));
        assertThat(value, is("1,50"));
    }

}