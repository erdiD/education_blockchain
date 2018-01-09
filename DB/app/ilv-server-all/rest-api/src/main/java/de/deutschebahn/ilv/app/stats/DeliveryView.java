package de.deutschebahn.ilv.app.stats;

import de.deutschebahn.ilv.domain.ContractType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Created by AlbertLacambraBasil on 21.08.2017.
 */
public class DeliveryView {

    private int projectDuration;
    private BigDecimal budget;
    private Map<String, BigDecimal> performedPaymentPerMonth;
    private BigDecimal totalPaidInPercent;
    private BigDecimal totalProgressInPercent;
    private ContractType contractType;

    public DeliveryView(ContractType contractType, int projectDuration, BigDecimal budget, Map<String, BigDecimal> performedPaymentPerMonth,
    					BigDecimal totalPaidInPercent, BigDecimal totalProgressInPercent) {
    	
        this.contractType = contractType;
    	this.performedPaymentPerMonth = performedPaymentPerMonth;
        this.totalPaidInPercent = totalPaidInPercent;
        this.totalProgressInPercent = totalProgressInPercent;

        Objects.requireNonNull(budget);

        this.projectDuration = projectDuration;
        this.budget = budget;
    }

    public int getProjectDuration() {
        return projectDuration;
    }

    public BigDecimal getBudget() {
        return this.budget;
    }

    public Map<String, BigDecimal> getPerformedPaymentPerMonth() {
        return performedPaymentPerMonth;
    }

    public BigDecimal getTotalPaidInPercent() {
        return totalPaidInPercent;
    }

    public DeliveryView setBudget(BigDecimal budget) {
    	this.budget = budget;
        return this;
    }
    
	public BigDecimal getTotalProgressInPercent() {
		return totalProgressInPercent;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

    public static class DeliveryViewBuilder {
        private int projectDuration;
        private BigDecimal budget;
        private Map<String, BigDecimal> performedPaymentPerMonth;
        private BigDecimal totalPaidInPercent;
        private BigDecimal totalProgressInPercent;
        private ContractType contractType;

    	public DeliveryViewBuilder setContractType(ContractType contractType) {
    		this.contractType = contractType;
    		return this;
    	}
    	
        /** returns the total duration in days.*/
        public DeliveryViewBuilder setProjectDuration(int projectDuration) {
            this.projectDuration = projectDuration;
            return this;
        }

        public DeliveryViewBuilder setBudget(BigDecimal budget) {
            this.budget = budget;
            return this;
        }

        public DeliveryViewBuilder setPerformedPaymentPerMonth(Map<String, BigDecimal> performedPaymentPerMonth) {
            this.performedPaymentPerMonth = performedPaymentPerMonth;
            return this;
        }

        public DeliveryViewBuilder setTotalPaidInPercent(BigDecimal totalPaidInPercent) {
            this.totalPaidInPercent = totalPaidInPercent;
            return this;
        }

        public DeliveryView createDeliveryView() {
            return new DeliveryView(contractType, projectDuration, budget, performedPaymentPerMonth, totalPaidInPercent, totalProgressInPercent);
        }

		public DeliveryViewBuilder setProgressInPercent(BigDecimal totalProgressInPercent) {
			this.totalProgressInPercent = totalProgressInPercent;
			return this;
		}
    }

}
