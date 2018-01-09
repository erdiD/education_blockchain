package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.bussinesobject.DateHelper;
import de.deutschebahn.ilv.bussinesobject.StatsFacade;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by KSchwartz
 */
@Ignore("Delivery not yet integrated")
public class DeliveryTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCalculatePaymentPerMonth() {
        Delivery delivery = new Delivery();
        delivery.setBudget(BigDecimal.valueOf(100));

        LocalDate startDateLocal = LocalDate.of(2016, 6, 24);
        Date startDate = DateHelper.toDate(startDateLocal);
        delivery.setStartDate(startDate);

        LocalDate deliveryDateLocal = LocalDate.of(2017, 1, 1);
        Date deliveryDate = DateHelper.toDate(deliveryDateLocal);
        delivery.setDeliveryDate(deliveryDate);

        StatsFacade statsFacade = new StatsFacade();
        delivery = StatsFacade.createWeeklyDeliveryEntries(delivery);

        HashMap<String, BigDecimal> entries = (HashMap<String, BigDecimal>) statsFacade.getWeeklyUsedBudget(delivery);

        //TODO: implement it correctly
        List<DeliveryEntry> deliveryEntries = Collections.emptyList();
        List<BigDecimal> deliveryEntriesValues = deliveryEntries
                .stream()
                .map(de -> de.getPaymentValue())
                .collect(Collectors.toList());

        assertThat("Budget", delivery.getBudget().toString(), is("100"));
        assertThat("Number of Delivery Entries", entries.size(), is(greaterThan(0)));
        assertThat("Payment for first week", deliveryEntriesValues.get(0).toString(), is("3"));
        assertThat("Payment for last week", deliveryEntriesValues.get(deliveryEntriesValues.size() - 1).toString(), is("19"));
    }

    @Test
    public void testWeeklyClassifier() {

        StatsFacade statsFacade = new StatsFacade();

        LocalDate deliveryDateLocal = LocalDate.of(2017, 1, 1);
        Date deliveryDate = DateHelper.toDate(deliveryDateLocal);

        DeliveryEntry testEntry = StatsFacade.createDelivery(10, DateHelper.toLocalDate(deliveryDate), BigDecimal.valueOf(100));
        String weeklyClassifierDate = statsFacade.getWeeklyClassifierDate(testEntry);
        assertThat("01.01.2017 shall be 2016-52", weeklyClassifierDate, is("2016-52"));
    }


}
