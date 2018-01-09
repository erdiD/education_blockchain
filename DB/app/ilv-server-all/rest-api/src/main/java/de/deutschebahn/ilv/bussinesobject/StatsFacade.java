package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.bussinesobject.delivery.DeliveryFacade;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.DeliveryEntry;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Created by AlbertLacambraBasil on 18.08.2017.
 */
public class StatsFacade {

    @Inject
    Logger logger;

    @Inject
    DeliveryFacade deliveryFacade;

    BigDecimal calculateTotalPaidUntilNow(Delivery delivery) {

        Objects.requireNonNull(delivery);

        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
        BigDecimal totalPaid = deliveryEntries
                .stream()
                .map(DeliveryEntry::getPaymentValue)
                .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));

        return totalPaid;

    }


    public BigDecimal getTotalProgressUntilNowInPercent(Delivery delivery) {

        Objects.requireNonNull(delivery);
        BigDecimal totalProgress = searchForLastDeliveryEntry(delivery);

        if(totalProgress == null){
        	 return BigDecimal.ZERO;
        }
        return totalProgress.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    private BigDecimal searchForLastDeliveryEntry(Delivery delivery) {

        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
        List<DeliveryEntry> sortedList = deliveryEntries.stream().sorted((de1, de2) -> {
            long x = de1.getDeliveryDate().getTime() - de2.getDeliveryDate().getTime();
            if (x < 0) return (int) -1;
            if (x > 0) return (int) 1;
            return (int) 0;
        }).collect(Collectors.toList());

        if (sortedList.size() == 0) {
            return null;
        }
        return sortedList.get(sortedList.size() - 1).getAchievedScope();
    }

    /**
     * Takes the highes Value in the DeliveryEntries as Accomplished Scope
     *
     * @param delivery
     * @return
     */
    public BigDecimal getAccomplishedScopeInPercent(Delivery delivery) {

        Objects.requireNonNull(delivery);
        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
        return deliveryEntries.stream().map(DeliveryEntry::getAchievedScope).reduce(
                BigDecimal.ZERO, (d1, d2) -> d1.compareTo(d2) > 0 ? d1 : d2
        );
    }

    /**
     * Get the last entry as Total Accomplished Scope
     * 
     * @param delivery
     * @return
     */
    public BigDecimal getTotalPaidUntilNowInPercent(Delivery delivery) {

        Objects.requireNonNull(delivery);
        BigDecimal totalPaid = calculateTotalPaidUntilNow(delivery);
        BigDecimal totalBudget = delivery.getBudget();
        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("[getTotalPaidUntilNowInPercent] Given budget is 0. Using One for calculations. # Delibery=" + delivery);
            totalBudget = BigDecimal.ONE;
        }
        return totalPaid.setScale(2).multiply(BigDecimal.valueOf(100)).divide(totalBudget.setScale(2), 2, BigDecimal.ROUND_HALF_DOWN);
    }

    public Map<String, BigDecimal> getMonthlyAccomplishedScope(Delivery delivery) {

        Objects.requireNonNull(delivery);

        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
        return deliveryEntries.stream()
                .collect(groupingBy(this::getClassifierDate,
                        mapping(DeliveryEntry::getAchievedScope, reducing(BigDecimal.ZERO, (d1, d2) -> d1.compareTo(d2) > 0 ? d1 : d2))));

    }

    public Map<String, BigDecimal> getWeeklyAccomplishedScope(Delivery delivery) {

        Objects.requireNonNull(delivery);
        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();

        return deliveryEntries.stream()
                .collect(groupingBy(this::getWeeklyClassifierDate,
                        mapping(DeliveryEntry::getAchievedScope, reducing(BigDecimal.ZERO, (d1, d2) -> d1.compareTo(d2) > 0 ? d1 : d2))));

    }

    public Map<String, BigDecimal> getMonthlyUsedBudget(Delivery delivery) {

        Objects.requireNonNull(delivery);

        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
        Map<String, BigDecimal> classifiedDeliveryEntries =
                deliveryEntries
                        .stream()
                        .collect(groupingBy(this::getClassifierDate,
                                mapping(DeliveryEntry::getPaymentValue, reducing(BigDecimal.ZERO, BigDecimal::add))));

        return classifiedDeliveryEntries;
    }

    public Map<String, BigDecimal> getWeeklyUsedBudget(Delivery delivery) {

        Objects.requireNonNull(delivery);
        List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();

        Map<String, BigDecimal> classifiedDeliveryEntries =
                deliveryEntries
                        .stream()
                        .collect(groupingBy(this::getWeeklyClassifierDate,
                                mapping(DeliveryEntry::getPaymentValue, reducing(BigDecimal.ZERO, BigDecimal::add))));

        return classifiedDeliveryEntries;
    }

    public String getWeeklyClassifierDate(DeliveryEntry deliveryEntry) {

		Calendar calendar = new GregorianCalendar(Locale.GERMAN);
		calendar.setTime(deliveryEntry.getDeliveryDate());
		int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        String classifierDate = calendar.getWeekYear() + "-" + String.format("%02d", weekOfYear);
        
        return classifierDate;
    }
    
    private String getClassifierDate(DeliveryEntry deliveryEntry) {

        LocalDate deliveryDate = DateHelper.toLocalDate(deliveryEntry.getDeliveryDate());
        String classifierDate = deliveryDate.format(DateHelper.MONTH_YEAR_FORMATTER);

        return classifierDate;
    }

    public Period getProjectDuration(Delivery delivery) {

        LocalDate startDate = DateHelper.toLocalDate(delivery.getStartDate());
        LocalDate deliveryDate = DateHelper.toLocalDate(delivery.getDeliveryDate());

        return Period.between(startDate, deliveryDate);
    }
    
    public int getProjectDurationInDays(Delivery delivery) {

        LocalDate startDate = DateHelper.toLocalDate(delivery.getStartDate());
        LocalDate deliveryDate = DateHelper.toLocalDate(delivery.getDeliveryDate());
        long totalDays = ChronoUnit.DAYS.between(startDate, deliveryDate);
        
        return (int) totalDays;
    }

    public BigDecimal getTotalUsedTimeInPercent(Delivery delivery) {

        BigDecimal totalDuration = new BigDecimal(getProjectDurationInDays(delivery));
        LocalDate startDate = DateHelper.toLocalDate(delivery.getStartDate());
        LocalDate today = LocalDate.now();

        if (startDate.isAfter(today)) {
            return BigDecimal.ZERO;
        }

        BigDecimal durationUntilNow = new BigDecimal((int) ChronoUnit.DAYS.between(startDate, today)).setScale(2);
        return durationUntilNow.multiply(new BigDecimal(100)).divide(totalDuration, 2, RoundingMode.HALF_UP);
    }


    /**
     * Used for SUBSCRIPTION_CONTRACTs / Leistungsscheine to crate a linear Statistic.
     *
     * @param delivery
     * @return
     */
    public static Delivery createWeeklyDeliveryEntries(Delivery delivery) {

        LocalDate originalStartDateLocal = DateHelper.toLocalDate(delivery.getStartDate());
        TemporalField fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek();

        // this fixes a bug, that 01.01.2017 would be shown as 2017-52
        LocalDate startDateLocal = originalStartDateLocal.with(fieldISO, 1);

        LocalDate currentDate = DateHelper.toLocalDate(new Date());
        LocalDate endDate = DateHelper.toLocalDate(delivery.getDeliveryDate());
        long totalWeekDiff = ChronoUnit.WEEKS.between(startDateLocal, endDate) + 1;

        if (startDateLocal.isEqual(endDate)) {
            DeliveryEntry singleDeliveryEntry = createDelivery(10, startDateLocal, delivery.getBudget());
            List<DeliveryEntry> deliveryEntries = delivery.getDeliveryEntries();
            deliveryEntries.add(singleDeliveryEntry);
            delivery.setDeliveryEntries(deliveryEntries);
            return delivery;
        }

        long budgetAsLong = delivery.getBudget().longValueExact();
        long paymentPerWeek = budgetAsLong;
        int scopePerWeek = 100;
        long remainingMoney = 0;

        if (totalWeekDiff > 0) {
            paymentPerWeek = budgetAsLong / totalWeekDiff;
            remainingMoney = budgetAsLong % totalWeekDiff;
            scopePerWeek = 100 / (int) totalWeekDiff;
        }

        ArrayList<DeliveryEntry> deList = new ArrayList<>();
        int week = 0;

        while (startDateLocal.plusWeeks(week).isBefore(currentDate)) {
            deList.add(createDelivery(scopePerWeek, startDateLocal.plusWeeks(week), new BigDecimal(paymentPerWeek)));
            week++;
        }

        if (!startDateLocal.plusWeeks(week).isBefore(endDate)) {
            // for the last week
            deList.add(createDelivery(100, startDateLocal.plusWeeks(week),
                    new BigDecimal(paymentPerWeek).add(new BigDecimal(remainingMoney))));
        }

        delivery.setDeliveryEntries(deList);
        return delivery;
    }

    /**
     * Used to create a Delivery for SUBSCRIPTION_CONTRACTs / Leistungsscheine
     *
     * @param achievedScope
     * @param deliveryDate
     * @param paymentValue
     * @return
     */
    public static DeliveryEntry createDelivery(int achievedScope, LocalDate deliveryDate, BigDecimal paymentValue) {

        DeliveryEntry deliveryEntry = new DeliveryEntry();
        deliveryEntry.setAchievedScope(new BigDecimal(achievedScope));
        deliveryEntry.setDeliveryDate(DateHelper.toDate(deliveryDate));
        deliveryEntry.setPaymentValue(paymentValue);

        return deliveryEntry;
    }


}
