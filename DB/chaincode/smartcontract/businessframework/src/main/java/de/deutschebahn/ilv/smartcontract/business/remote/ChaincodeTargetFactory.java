package de.deutschebahn.ilv.smartcontract.business.remote;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AlbertLacambraBasil on 06.10.2017.
 */
public class ChaincodeTargetFactory {

    private final Map<String, ChaincodeTarget> targets;

    public ChaincodeTargetFactory() {
        targets = new ConcurrentHashMap<>();
    }

    public ChaincodeTarget getOrCreate(String chaincodeName) {
        Objects.requireNonNull(chaincodeName);
        return targets.computeIfAbsent(chaincodeName, ChaincodeTarget::new);
    }
}
