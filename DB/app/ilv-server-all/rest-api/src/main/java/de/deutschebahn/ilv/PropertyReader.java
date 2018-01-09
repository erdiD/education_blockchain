package de.deutschebahn.ilv;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by AlbertLacambraBasil on 24.07.2017.
 */
public class PropertyReader {

    public static <T> T getValueOrException(String key, Function<String, T> propertyProvider) {
        T value = propertyProvider.apply(key);
        Objects.requireNonNull(value, "Key must be given:" + key);
        return value;
    }

}
