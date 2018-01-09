package de.deutschebahn.ilv.smartcontract.business;

/**
 * Created by AlbertLacambraBasil on 20.10.2017.
 */
public interface LocalObjectUpdatedHandler<T> {
    void handle(T object);
}
