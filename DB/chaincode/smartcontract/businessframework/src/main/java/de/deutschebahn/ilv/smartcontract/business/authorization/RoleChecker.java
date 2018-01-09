package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.smartcontract.business.statemanagment.FlowStep;

/**
 * Created by AlbertLacambraBasil on 13.06.2017.
 */
public interface RoleChecker {

    boolean hasRequiredRoleToRunActionFlow(FlowStep flow);

}
