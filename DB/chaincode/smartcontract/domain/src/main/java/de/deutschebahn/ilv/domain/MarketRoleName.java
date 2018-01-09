package de.deutschebahn.ilv.domain;

/**
 * Created by AlbertLacambraBasil on 09.06.2017.
 */
public enum MarketRoleName {

    //Customer demand roles
    DEMAND_CREATOR(RoleType.CUSTOMER),
    CUSTOMER_SIGNER(RoleType.CUSTOMER),
    CUSTOMER_OFFER_TECHNICAL_APPROVAL(RoleType.CUSTOMER),
    CUSTOMER_OFFER_COMMERCIAL_APPROVAL(RoleType.CUSTOMER),

    OFFER_CREATOR(RoleType.SUPPLIER),
    SUPPLIER_SIGNER(RoleType.SUPPLIER),
    SUPPLIER_PROJECT_MANAGER(RoleType.SUPPLIER),
    SUPPLIER_BIG_BOSS(RoleType.SUPPLIER),
    SUPPLIER_OFFER_APPROVAL(RoleType.SUPPLIER),
    PEER(RoleType.PEER),

    //MAAS specific roles
    SUPPLIER_CONTROLLER(RoleType.SUPPLIER),
    DEMAND_CONTROLLER(RoleType.CUSTOMER),
    DEMAND_APPROVAL(RoleType.CUSTOMER),

    NONE(RoleType.NONE);

    RoleType roleType;

    MarketRoleName(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
