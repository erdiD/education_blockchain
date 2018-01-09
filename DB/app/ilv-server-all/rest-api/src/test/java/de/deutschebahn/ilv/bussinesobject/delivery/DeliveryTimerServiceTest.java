package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.domain.Delivery;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.Timer;
import javax.ejb.TimerService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DeliveryTimerServiceTest {

    DeliveryTimerService cut;

    @Before
    public void setUp() throws Exception {
        cut = new DeliveryTimerService();
        cut.timerService = mock(TimerService.class);
        cut.logger = Logger.getLogger(DeliveryTimerService.class.getSimpleName());
    }

    @Test
    public void setDeliveryTimers() throws Exception {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + Duration.ofSeconds(5).toMillis());
        Delivery delivery = new Delivery();
        delivery.setId("id");
        delivery.setStartDate(startDate);
        delivery.setDeliveryDate(endDate);
        cut.setDeliveryTimers(delivery);

        //Start timer is deactivated
//        verify(cut.timerService, times(1)).createSingleActionTimer(eq(startDate), any());
        verify(cut.timerService, times(1)).createSingleActionTimer(eq(endDate), any());
    }

    @Test
    public void cancelTimers() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setId("1");
        Timer startTimer = mock(Timer.class);
        Timer endTimer = mock(Timer.class);
        when(startTimer.getInfo()).thenReturn("START_DELIVERY##Delivery##1");
        when(endTimer.getInfo()).thenReturn("END_DELIVERY##Delivery##1");
        List<Timer> timerList = new ArrayList<>();
        timerList.add(startTimer);
        timerList.add(endTimer);

        when(cut.timerService.getTimers()).thenReturn(timerList);
        cut.cancelTimers(delivery);

        verify(startTimer).cancel();
        verify(endTimer).cancel();
    }
}