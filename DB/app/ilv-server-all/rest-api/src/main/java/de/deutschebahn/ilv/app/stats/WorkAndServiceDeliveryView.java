package de.deutschebahn.ilv.app.stats;

import de.deutschebahn.ilv.domain.ContractType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 21.08.2017.
 */
public class WorkAndServiceDeliveryView extends DeliveryView {

    private BigDecimal achievedScope;
    private Map<String, BigDecimal> achievedScopePerMonth;

    public WorkAndServiceDeliveryView(ContractType contractType,
    								  int projectDuration,
                                      BigDecimal budget,
                                      BigDecimal totalPaidInPercent,
                                      BigDecimal totalProgressInPercent,
                                      BigDecimal achievedScope,
                                      Map<String, BigDecimal> achievedScopePerMonth,
                                      Map<String, BigDecimal> performedPaymentPerMonth) {

        super(contractType, projectDuration, budget, performedPaymentPerMonth, totalPaidInPercent, totalProgressInPercent);

        Objects.requireNonNull(totalPaidInPercent);
        Objects.requireNonNull(achievedScope);
        Objects.requireNonNull(achievedScopePerMonth);

        this.achievedScope = achievedScope;
        this.achievedScopePerMonth = achievedScopePerMonth;
    }

    public BigDecimal getAchievedScope() {
        return achievedScope;
    }

    public Map<String, BigDecimal> getAchievedScopePerMonth() {
        return achievedScopePerMonth;
    }

    public static class WorkAndServiceDeliveryViewBuilder {
        private int projectDuration;
        private BigDecimal budget;
        private BigDecimal totalPaidInPercent;
        private BigDecimal totalProgressInPercent;
        private BigDecimal achievedScope;
        private Map<String, BigDecimal> achievedScopePerMonth;
        private Map<String, BigDecimal> performedPaymentPerMonth;
		private ContractType contractType;

        public WorkAndServiceDeliveryViewBuilder setProjectDuration(int projectDuration) {
            this.projectDuration = projectDuration;
            return this;
        }

        public WorkAndServiceDeliveryViewBuilder setBudget(BigDecimal budget) {
            this.budget = budget;
            return this;
        }

        public WorkAndServiceDeliveryViewBuilder setTotalPaidInPercent(BigDecimal totalPaidInPercent) {
            this.totalPaidInPercent = totalPaidInPercent;
            return this;
        }

        public WorkAndServiceDeliveryViewBuilder setAchievedScope(BigDecimal achievedScope) {
            this.achievedScope = achievedScope;
            return this;
        }

        public WorkAndServiceDeliveryViewBuilder setAchievedScopePerMonth(Map<String, BigDecimal> achievedScopePerMonth) {
            this.achievedScopePerMonth = achievedScopePerMonth;
            return this;
        }

        public WorkAndServiceDeliveryViewBuilder setPerformedPaymentPerMonth(Map<String, BigDecimal> performedPaymentPerMonth) {
            this.performedPaymentPerMonth = performedPaymentPerMonth;
            return this;
        }

        public WorkAndServiceDeliveryView createWorkAndServiceDeliveryView() {
            return new WorkAndServiceDeliveryView(contractType, projectDuration, budget, totalPaidInPercent, totalProgressInPercent, achievedScope, achievedScopePerMonth, performedPaymentPerMonth);
        }

		public WorkAndServiceDeliveryViewBuilder setContractType(ContractType contractType) {
			this.contractType = contractType;
			return this;
		}
		
		public WorkAndServiceDeliveryViewBuilder setProgressInPercent(BigDecimal totalProgressInPercent) {
			this.totalProgressInPercent = totalProgressInPercent;
			return this;
		}
    }
}
