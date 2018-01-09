package de.deutschebahn.ilv.smartcontract.business.statemanagment;

import de.deutschebahn.ilv.domain.BusinessObject;

/**
 * Created by alacambra on 04.06.17.
 */
public class BusinessObjectStateTransitionEvent<T extends BusinessObject> {

    private T businessObject;

    public BusinessObjectStateTransitionEvent(T businessObject) {
        this.businessObject = businessObject;
    }

    public T getBusinessObject() {
        return businessObject;
    }

    @Override
    public String toString() {
        return "BusinessObjectStateTransitionEvent{" +
                "businessObject=" + businessObject +
                '}';
    }
}