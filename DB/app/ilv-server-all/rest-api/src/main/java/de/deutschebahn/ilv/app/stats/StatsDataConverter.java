package de.deutschebahn.ilv.app.stats;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by AlbertLacambraBasil on 21.08.2017.
 */
public class StatsDataConverter {

    public JsonObject serialize(ServiceDeliveryView deliveryView) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
        		.add("contractType", deliveryView.getContractType().name())
                .add("totalPaid", deliveryView.getTotalPaidInPercent().toString())
                .add("totalUsedTime", deliveryView.getTotalUsedTimeInPercent().toString())
                .add("totalProgress", deliveryView.getTotalUsedTimeInPercent().toString())
                .add("budget", deliveryView.getBudget().toString())
                .add("duration", deliveryView.getProjectDuration())
                .add("paymentsPerMonth", createMonthValueArray(deliveryView.getPerformedPaymentPerMonth()));

        return builder.build();
    }

    public JsonObject serialize(WorkAndServiceDeliveryView deliveryView) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
        		.add("contractType", deliveryView.getContractType().name())
                .add("totalPaid", deliveryView.getTotalPaidInPercent().toString())
                .add("achieved", deliveryView.getAchievedScope().toString())
                .add("totalProgress", deliveryView.getTotalProgressInPercent().toString())
                .add("budget", deliveryView.getBudget().toString())
                .add("duration", deliveryView.getProjectDuration())
                .add("scopePerMonth", createMonthValueArray(deliveryView.getAchievedScopePerMonth()))
                .add("paymentsPerMonth", createMonthValueArray(deliveryView.getPerformedPaymentPerMonth()));

        return builder.build();
    }

    public JsonObject serialize(DeliveryView deliveryView) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
        		.add("contractType", deliveryView.getContractType().name())
                .add("totalPaid", deliveryView.getTotalPaidInPercent().toString())
                .add("totalProgress", deliveryView.getTotalProgressInPercent().toString())
                .add("budget", deliveryView.getBudget().toString())
                .add("duration", deliveryView.getProjectDuration())
                .add("paymentsPerMonth", createMonthValueArray(deliveryView.getPerformedPaymentPerMonth()));

        return builder.build();
    }

    private JsonObject createMonthValueArray(Map<String, BigDecimal> values) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        values.entrySet()
                .stream()
                .forEach(entry -> builder.add(entry.getKey(), entry.getValue().toString()));

        return builder.build();
    }

}
