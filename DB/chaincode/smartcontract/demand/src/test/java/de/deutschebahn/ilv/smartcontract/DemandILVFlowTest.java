package de.deutschebahn.ilv.smartcontract;

import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;
import de.deutschebahn.ilv.smartcontract.business.demand.DemandILVFlow;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.FlowStep;
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
public class DemandILVFlowTest {

    DemandILVFlow cut;

    @Test
    public void actionsAreLoaded() throws Exception {
        cut = new DemandILVFlow();
        Map<ObjectStateTransitionAction, Set<FlowStep>> flowSteps = cut.getFlowSteps();
        assertThat(flowSteps.keySet(), not(empty()));
        assertThat(flowSteps.keySet().toString(), flowSteps.keySet().size(), is(7));
    }

}