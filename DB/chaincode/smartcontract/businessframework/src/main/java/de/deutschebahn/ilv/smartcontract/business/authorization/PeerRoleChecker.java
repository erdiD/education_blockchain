package de.deutschebahn.ilv.smartcontract.business.authorization;

import de.deutschebahn.ilv.domain.MarketRoleName;
import de.deutschebahn.ilv.smartcontract.business.statemanagment.FlowStep;

/**
 * Created by AlbertLacambraBasil on 13.06.2017.
 */
public class PeerRoleChecker implements RoleChecker {

    @Override
    public boolean hasRequiredRoleToRunActionFlow(FlowStep flow) {
        return flow.getRole() == MarketRoleName.PEER;
    }
}
