package de.deutschebahn.ilv.bussinesobject;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DateHelper {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Berlin");
    public static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");
    public static final DateTimeFormatter WEEK_OF_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-w");

    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        return toDate(toLocalDateTime(localDate));
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZONE_ID);
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDate.from(toLocalDateTime(date));
    }

}
