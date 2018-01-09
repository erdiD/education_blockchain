package de.deutschebahn.ilv.smartcontract.business;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 23.10.2017.
 */
public class SplittedKey {


    String objectType;
    List<String> attributes = new ArrayList<>();

    public SplittedKey(String key) {
        //TODO: put separator into a global variable
        String[] split = key.toString().split("\u0000");
        objectType = split[0];
        if (split.length > 1) {
            attributes = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(split, 1, split.length)));
        }
    }

    public SplittedKey(CompositeKey compositeKey) {
        objectType = compositeKey.getObjectType();
        attributes = new ArrayList<>(compositeKey.getAttributes());
    }

    public CompositeKey asCompositeKey() {
        return new CompositeKey(objectType, attributes);
    }

    public SplittedKey addAttribute(String attribute) {
        attributes.add(attribute);
        return this;
    }

    public String asString() {
        return asCompositeKey().toString();
    }

    @Override
    public String toString() {
        return "SplittedKey{" +
                "objectType='" + objectType + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
