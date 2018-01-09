/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.ObjectState;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author AlbertLacambraBasil
 */
public class SerializationHelper {

    private static final Logger LOG = Logger.getLogger(SerializationHelper.class.getName());
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.y");

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Berlin");

    public static BigDecimal convertToBigDecimal(String value) {


        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }

        //Search for euro symbol
        int i = value.indexOf('\u20AC');

        if (i == 0) {
            return BigDecimal.ZERO;
        }

        if (i > 0) {
            value = value.substring(0, i);
        }

        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMANY);
        formatter.applyLocalizedPattern("#.#00,0#");
        formatter.setParseBigDecimal(true);
        BigDecimal parsedValue = null;

        try {
            parsedValue = (BigDecimal) formatter.parse(value);
        } catch (ParseException e) {
            LOG.info("[convertToBigDecimal] value has an invalid format. Defaulting to direct conversion. Value=" + value);
            parsedValue = new BigDecimal(value);
        }

        return parsedValue;
    }

    public static Date convertToDate(String dateString, DateTimeFormatter dateTimeFormatter) {
        Date date = fromLocalDateToDate(LocalDate.parse(dateString, dateTimeFormatter));
        return date;
    }

    public static Date convertToDateTime(String dateString, DateTimeFormatter dateTimeFormatter) {
        Date date = fromLocalDateTimeToDate(LocalDateTime.parse(dateString));
        return date;
    }

    public static Date convertToDateTime(String dateString) {
        return convertToDate(dateString, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String convertFromDate(Date date) {

        if (date == null) {
            date = new Date();
        }
        return DATE_FORMATTER.format(fromDateToLocalDate(date));
    }

    public static Date convertToDate(String date) {
        return convertToDate(date, DATE_FORMATTER);
    }

    public static String serializeBigDecimalCurrency(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            bigDecimal = BigDecimal.ZERO;
        }
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMANY);
        formatter.applyLocalizedPattern("#.##0,00");
        bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

        return formatter.format(bigDecimal);
    }

    public static JsonArray serializeToJsonArray(Collection<String> entities) {
        return entities.stream().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
    }

    public static List<String> deserializeToArray(JsonArray jsonArray) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            strings.add(jsonArray.getString(i));
        }
        return strings;
    }

    public static JsonArray serializeToJsonArrayWithJsonObjects(Collection<JsonObject> entities) {
        return entities.stream().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add).build();
    }

    public static LocalDate fromDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZONE_ID).toLocalDate();
    }

    public static LocalDateTime fromDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZONE_ID).toLocalDateTime();
    }

    public static Date fromLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZONE_ID).toInstant());
    }

    public static Date fromLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    public static String removeObjectTypeFromObjectState(ObjectState state) {
        String stateName = state.name();
        return stateName.replace("DEMAND_", "").replace("OFFER_", "");
    }

    public static String getFileHashAsString(byte[] hashBytes) {

        if (hashBytes == null) {
            return "";
        }

        StringBuilder digestStr = new StringBuilder();
        for (int i = 0; i < hashBytes.length; i++) {
            String hex = Integer.toHexString(0xff & hashBytes[i]);
            if (hex.length() == 1) digestStr.append('0');
            digestStr.append(hex);
        }
        return digestStr.toString();
    }

    public static <T> void serializeValueIfPresent(BiConsumer<String, T> builder, String key, T object) {
        if (object != null) {
            try {
                builder.accept(key, object);
            } catch (NullPointerException e) {
                //Only there in case applying transformation when passing the object, than can try to access the object
                // before to check for the null value
            }
        }
    }

    public static JsonObject stringToJsonObject(String input) {
        return bytesToJsonObject(input.getBytes());
    }

    public static JsonObject bytesToJsonObject(byte[] input) {
        Objects.requireNonNull(input);
        try {
            return Json.createReader(new ByteArrayInputStream(input)).readObject();
        } catch (JsonException e) {
            LOG.log(Level.SEVERE, "[bytesToJsonObject] Not possible to create jsonObject. ReceivedAsString=" + new String(input), e);
            throw e;
        }
    }

    public static <T, R> void serializeValueIfPresent(BiConsumer<String, R> builder, Function<T, R> converter, String key, T object) {
        if (object != null) {
            try {
                R convertedObject = converter.apply(object);
                builder.accept(key, convertedObject);
            } catch (NullPointerException e) {
                //Only there in case applying transformation when passing the object, than can try to access the object
                // before to check for the null value
            }
        }
    }

    /**
     * @param builder: the json value builder
     * @param key:     assigned key to the element to be added
     * @param object:  object to be added
     * @param <T>:     type of the object to be added
     */
    public static <T, R> void serializeValueOrException(BiConsumer<String, R> builder, Function<T, R> converter, String key, T object) {
        if (object == null) {
            throw ClientException.invalidValue(key, "null");
        }

        R convertedObject = converter.apply(object);
        builder.accept(key, convertedObject);
    }

    /**
     * @param builder:      the json value bilder
     * @param key:          assigned key to the element to be added
     * @param object:       object to be added
     * @param defaultValue: value to be used in case the object given is null
     * @param <T>:          type of the object to be added
     */
    public static <T> void serializeValueOrDefault(BiConsumer<String, T> builder, String key, T object, T defaultValue) {
        Objects.requireNonNull(defaultValue);
        if (object == null) {
            object = defaultValue;
        }
        builder.accept(key, object);
    }

    /**
     * @param builder:      JsonBuilder where the value must be added (e.g. jsonBuilder::add)
     * @param converter:    Function that converts the passed value, to a json compatible value
     * @param key:          key of the value in the jsonObject to be added
     * @param value:        value to be added
     * @param defaultValue: value to use in case the given value is null
     * @param <T>:          passed  value type
     * @param <R>:          added value type
     */
    public static <T, R> void serializeValueOrDefault(BiConsumer<String, R> builder, Function<T, R> converter, String key, T value, R defaultValue) {
        Objects.requireNonNull(defaultValue);
        R transformedValue;
        if (value == null) {
            transformedValue = defaultValue;
        } else {
            transformedValue = converter.apply(value);
        }
        builder.accept(key, transformedValue);
    }

    /**
     * @param builder: JsonBuilder where the value must be added (e.g. jsonBuilder::add)
     * @param key:     key of the value in the jsonObject to be added
     * @param value:   value to be added
     * @param <T>:     Type of the object to be added. It must be a supported json type.
     */
    public static <T> void serializeValueOrException(BiConsumer<String, T> builder, String key, T value) {
        if (value == null) {
            throw ClientException.invalidValue(key, "null");
        }
        builder.accept(key, value);
    }

    /**
     * @param key:     key of the json object
     * @param fetcher: function to get the value from the jsonObject (json::getString)
     * @param <T>
     * @return
     */
    public static <T> T getValueOrException(String key, Function<String, T> fetcher) {

        try {
            T value = fetcher.apply(key);
            return Objects.requireNonNull(value);
        } catch (NullPointerException | ClientException e) {
            throw ClientException.paramNotGivenException(key);
        }

    }

    /**
     * @param key:       key of the json object
     * @param fetcher:   function to get the value from the jsonObject (json::getString)
     * @param converter: Function to convert the fetched data to another type (Enum::valueOf).
     * @param <T>:       Type fetched from the JsonObject
     * @param <R>:       Type returned after the convertion (e.g. PaymentType)
     * @return
     */
    public static <T, R> R getValueOrException(String key, Function<String, T> fetcher, Function<T, R> converter) {
        T value = getValueOrException(key, fetcher);
        try {
            return converter.apply(value);
        } catch (IllegalArgumentException e) {
            throw ClientException.invalidValue(key, value.toString());
        }
    }

    /**
     * @param key:          key of the json object
     * @param fetcher:      function to get the value from the jsonObject (json::getString)
     * @param converter:    Function to convert the fetched data to another type (Enum::valueOf).
     * @param defaultValue: returned value in case null value has been provided
     * @param <T>:          Type fetched from the JsonObject
     * @param <R>:          Type returned after the convertion (e.g. PaymentType)
     * @return
     */
    public static <T, R> R getValueOrDefault(String key, Function<String, T> fetcher, Function<T, R> converter, R defaultValue) {
        Objects.requireNonNull(defaultValue);
        try {
            T value = getValueOrException(key, fetcher);
            return converter.apply(value);
        } catch (NullPointerException | ClientException e) {
            return defaultValue;
        }
    }

    /**
     * @param key:          key of the json object
     * @param fetcher:      function to get the value from the jsonObject (json::getString)
     * @param defaultValue: returned value in case null value has been provided
     * @param <T>:          Type fetched from the JsonObject
     * @return
     */
    public static <T> T getValueOrDefault(String key, Function<String, T> fetcher, T defaultValue) {
        Objects.requireNonNull(defaultValue);
        try {
            return getValueOrException(key, fetcher);
        } catch (NullPointerException | ClientException e) {
            return defaultValue;
        }
    }

    private static <T, R> R getValue(String key, Function<String, T> fetcher, Function<T, R> converter) {
        return converter.apply(fetcher.apply(key));
    }

    public static <T extends Enum<T>> T getValueOrException(Function<String, String> fetcher, String key, Class<T> enumType) {
        String value = getValueOrException(key, fetcher);
        return toEnum(enumType, value);
    }

    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String value) {
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw ClientException.invalidValue(value);
        }
    }
}