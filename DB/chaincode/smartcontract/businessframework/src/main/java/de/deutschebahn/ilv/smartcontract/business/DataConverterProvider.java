package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.smartcontract.commons.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlbertLacambraBasil on 09.10.2017.
 */
public class DataConverterProvider {

    private final Map<String, DataConverter<?>> converters;

    public DataConverterProvider() {
        converters = new HashMap<>();
        add(new OfferDataConverter());
        add(new DemandDataConverter());
        add(new ContractDataConverter());
        add(new DeliveryDataConverter());
        add(new AttachmentDataConverter());
        add(new UserDataConverter());
    }

    private <T> void add(DataConverter<T> dataConverter) {
        converters.put(dataConverter.getAssignedType(), dataConverter);
    }

    public <T> DataConverter<T> getDataConverter(String objectType) {
        return (DataConverter<T>) converters.get(objectType);
    }
}
