package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.bussinesobject.DateHelper;
import de.deutschebahn.ilv.domain.DeliveryEntry;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DeliveryHelper {


    public static DeliveryEntry createDelivery(int achievedScope, LocalDate deliveryDate, BigDecimal paymentValue) {

        DeliveryEntry deliveryEntry = new DeliveryEntry();
        deliveryEntry.setAchievedScope(new BigDecimal(achievedScope));
        deliveryEntry.setDeliveryDate(DateHelper.toDate(deliveryDate));
        deliveryEntry.setPaymentValue(paymentValue);

        return deliveryEntry;
    }

}
