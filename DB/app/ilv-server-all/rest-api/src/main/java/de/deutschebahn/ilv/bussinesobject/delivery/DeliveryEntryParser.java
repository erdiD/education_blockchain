package de.deutschebahn.ilv.bussinesobject.delivery;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
public class DeliveryEntryParser {

    @Inject
    Logger logger;

    public DeliveryParserResult parse(Collection<String> psps, InputStream inputStream, boolean isWorkOrServiceContract) {

        logger.info(String.format("[parse] Parsing stream for psps %s. isWorkAndServiceContract %b", psps, isWorkOrServiceContract));

        if (inputStream.markSupported()) {
            try {
                inputStream.reset();
            } catch (IOException e) {
                logger.warning("[parse] Problems resetting stream. Stream could be lost:" + e.getMessage());
            }
        }
        Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines();

        Map<Boolean, List<ParsedDeliveryEntry>> classifiedEntries = lines
                .map(l -> isWorkOrServiceContract ? new ParsedMilestoneDeliveryEntry(psps, l) : new ParsedDeliveryEntry(psps, l))
                .collect(groupingBy(ParsedDeliveryEntry::isValid));

        DeliveryParserResult deliveryParserResult = new DeliveryParserResult();

        classifiedEntries.computeIfAbsent(Boolean.FALSE, k -> new ArrayList<>(0))
                .stream()
                .map((ParsedDeliveryEntry parsedDeliveryEntry) -> new DeliveryEntryError(
                        parsedDeliveryEntry.getLine(),
                        parsedDeliveryEntry.getErrorMsg())
                ).forEach(deliveryParserResult::addError);

        classifiedEntries
                .computeIfAbsent(Boolean.TRUE, k -> new ArrayList<>(0))
                .stream()
                .map(ParsedDeliveryEntry::buildDeliveryEntry)
                .forEach(deliveryParserResult::addDeliveryEntry);


        return deliveryParserResult;
    }

}
