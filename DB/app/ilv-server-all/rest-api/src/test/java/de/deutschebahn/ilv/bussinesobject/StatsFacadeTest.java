package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryHelper;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.DeliveryEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by AlbertLacambraBasil on 21.08.2017.
 */
public class StatsFacadeTest {

    StatsFacade cut;
    Delivery delivery;

    @Before
    public void setUp() throws Exception {
        cut = new StatsFacade();
        delivery = mock(Delivery.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void calculateTotalPaidUntilNow() throws Exception {
        DeliveryEntry deliveryEntry1 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 5), new BigDecimal(15));
        DeliveryEntry deliveryEntry2 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 15), new BigDecimal(15));
        DeliveryEntry deliveryEntry3 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 12, 15), new BigDecimal(15));

        when(delivery.getDeliveryEntries()).thenReturn(Arrays.asList(new DeliveryEntry[]{deliveryEntry1, deliveryEntry2, deliveryEntry3}));
        assertThat(cut.calculateTotalPaidUntilNow(delivery), is(new BigDecimal(45)));
    }

    @Test
    public void calculateTotalPaidUntilNowInPercent() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setBudget(BigDecimal.valueOf(135));
        DeliveryEntry deliveryEntry1 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 5), new BigDecimal(15));
        DeliveryEntry deliveryEntry2 = DeliveryHelper.createDelivery(15, LocalDate.of(2016, 10, 15), new BigDecimal(15));
        DeliveryEntry deliveryEntry3 = DeliveryHelper.createDelivery(25, LocalDate.of(2016, 12, 15), new BigDecimal(15));
        delivery.setDeliveryEntries(Arrays.asList(new DeliveryEntry[]{deliveryEntry1, deliveryEntry2, deliveryEntry3}));

        assertThat(cut.getTotalPaidUntilNowInPercent(delivery), is(new BigDecimal("33.33")));
    }

    @Test
    public void getAccomplishedScopeInPercent() throws Exception {
        DeliveryEntry deliveryEntry1 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 5), new BigDecimal(15));
        DeliveryEntry deliveryEntry2 = DeliveryHelper.createDelivery(15, LocalDate.of(2016, 10, 15), new BigDecimal(15));
        DeliveryEntry deliveryEntry3 = DeliveryHelper.createDelivery(25, LocalDate.of(2016, 12, 15), new BigDecimal(15));
        when(delivery.getDeliveryEntries()).thenReturn(Arrays.asList(new DeliveryEntry[]{deliveryEntry1, deliveryEntry2, deliveryEntry3}));

        assertThat(cut.getAccomplishedScopeInPercent(delivery), is(new BigDecimal(25)));
    }

    @Test
    public void getMonthlyAccomplishedScope() throws Exception {
        DeliveryEntry deliveryEntry1 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 5), new BigDecimal(15));
        DeliveryEntry deliveryEntry2 = DeliveryHelper.createDelivery(15, LocalDate.of(2016, 10, 15), new BigDecimal(15));
        DeliveryEntry deliveryEntry3 = DeliveryHelper.createDelivery(5, LocalDate.of(2016, 12, 15), new BigDecimal(15));
        when(delivery.getDeliveryEntries()).thenReturn(Arrays.asList(new DeliveryEntry[]{deliveryEntry1, deliveryEntry2, deliveryEntry3}));

        assertThat(cut.getMonthlyAccomplishedScope(delivery).size(), is(2));
        assertThat(cut.getMonthlyAccomplishedScope(delivery).get("10-2016"), is(new BigDecimal(15)));
        assertThat(cut.getMonthlyAccomplishedScope(delivery).get("12-2016"), is(new BigDecimal(5)));
    }

    @Test
    public void getMonthlyUsedBudget() throws Exception {
        DeliveryEntry deliveryEntry1 = DeliveryHelper.createDelivery(10, LocalDate.of(2016, 10, 5), new BigDecimal(15));
        DeliveryEntry deliveryEntry2 = DeliveryHelper.createDelivery(15, LocalDate.of(2016, 10, 15), new BigDecimal(55));
        DeliveryEntry deliveryEntry3 = DeliveryHelper.createDelivery(5, LocalDate.of(2016, 12, 15), new BigDecimal(135));
        when(delivery.getDeliveryEntries()).thenReturn(Arrays.asList(new DeliveryEntry[]{deliveryEntry1, deliveryEntry2, deliveryEntry3}));

        assertThat(cut.getMonthlyUsedBudget(delivery).size(), is(2));
        assertThat(cut.getMonthlyUsedBudget(delivery).get("10-2016"), is(new BigDecimal(70)));
        assertThat(cut.getMonthlyUsedBudget(delivery).get("12-2016"), is(new BigDecimal(135)));
    }

}