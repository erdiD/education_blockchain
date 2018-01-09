package de.deutschebahn.ilv;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by AlbertLacambraBasil on 07.07.2017.
 */
public class CurrencyTest {

    @Test
    public void format() {
        BigDecimal num = new BigDecimal("20000000.0023100123");
        System.out.println(num.toString());
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMANY);

        System.out.println(formatter.format(num));
    }
}
