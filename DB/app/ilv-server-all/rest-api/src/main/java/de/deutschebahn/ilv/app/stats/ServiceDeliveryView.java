package de.deutschebahn.ilv.app.stats;

import de.deutschebahn.ilv.domain.ContractType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;


/**
 * Created by AlbertLacambraBasil on 21.08.2017.
 */
public class ServiceDeliveryView extends DeliveryView {

    private BigDecimal totalPaidInPercent;
    private BigDecimal totalUsedTimeInPercent;

    public ServiceDeliveryView(ContractType contractType,
    						   int projectDuration,
                               BigDecimal budget,
                               BigDecimal totalPaidInPercent,
                               BigDecimal totalProgressInPercent,
                               BigDecimal totalUsedTimeInPercent,
                               Map<String, BigDecimal> performedPaymentPerMonth) {

        super(contractType, projectDuration, budget, performedPaymentPerMonth, totalPaidInPercent, totalProgressInPercent);

        Objects.requireNonNull(totalPaidInPercent);
        Objects.requireNonNull(performedPaymentPerMonth);
        Objects.requireNonNull(totalUsedTimeInPercent);

        this.totalUsedTimeInPercent = totalUsedTimeInPercent;
        this.totalPaidInPercent = totalPaidInPercent;
    }

    public BigDecimal getTotalPaidInPercent() {
        return totalPaidInPercent;
    }

    public BigDecimal getTotalUsedTimeInPercent() {
        return totalUsedTimeInPercent;
    }

    public static class ServiceDeliveryViewBuilder {
        private int projectDuration;
        private BigDecimal budget;
        private BigDecimal totalPaidInPercent;
        private BigDecimal totalProgressInPercent;
        private BigDecimal totalUsedTimeInPercent;
        private Map<String, BigDecimal> performedPaymentPerMonth;
		private ContractType contractType;

        public ServiceDeliveryViewBuilder setProjectDuration(int projectDuration) {
            this.projectDuration = projectDuration;
            return this;
        }

        public ServiceDeliveryViewBuilder setBudget(BigDecimal budget) {
            this.budget = budget;
            return this;
        }

        public ServiceDeliveryViewBuilder setTotalPaidInPercent(BigDecimal totalPaidInPercent) {
            this.totalPaidInPercent = totalPaidInPercent;
            return this;
        }

        public ServiceDeliveryViewBuilder setTotalUsedTimeInPercent(BigDecimal totalUsedTimeInPercent) {
            this.totalUsedTimeInPercent = totalUsedTimeInPercent;
            return this;
        }

        public ServiceDeliveryViewBuilder setPerformedPaymentPerMonth(Map<String, BigDecimal> performedPaymentPerMonth) {
            this.performedPaymentPerMonth = performedPaymentPerMonth;
            return this;
        }

        public ServiceDeliveryView createServiceDeliveryView() {
            return new ServiceDeliveryView(contractType, projectDuration, budget, totalPaidInPercent, totalProgressInPercent, totalUsedTimeInPercent, performedPaymentPerMonth);
        }

		public ServiceDeliveryViewBuilder setContractType(ContractType contractType) {
			this.contractType = contractType;
			return this;
		}
		
		public ServiceDeliveryViewBuilder setProgressInPercent(BigDecimal totalProgressInPercent) {
			this.totalProgressInPercent = totalProgressInPercent;
			return this;
		}
		
    }
}
