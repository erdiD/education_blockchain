package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.FlowStep;
import de.deutschebahn.ilv.smartcontract.offer.OfferILVFlow;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by AlbertLacambraBasil on 04.08.2017.
 */
public class OfferILVFlowTest {

    OfferILVFlow cut;

    @Test
    public void actionsAreLoaded() throws Exception {
        cut = new OfferILVFlow();
        Map<ObjectStateTransitionAction, Set<FlowStep>> flowSteps = cut.getFlowSteps();

        assertThat(flowSteps.keySet(), not(empty()));

        assertThat(flowSteps.keySet().toString(), flowSteps.keySet().size(), is(11));
        assertThat(flowSteps.get(ObjectStateTransitionAction.APPROVE_OFFER).toString(),
                flowSteps.get(ObjectStateTransitionAction.APPROVE_OFFER).size(), is(4));
    }

}