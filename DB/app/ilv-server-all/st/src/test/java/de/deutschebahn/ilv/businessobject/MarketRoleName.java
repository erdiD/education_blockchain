package de.deutschebahn.ilv.businessobject;

import static de.deutschebahn.ilv.businessobject.RoleType.SUPPLIER;

/**
 * Created by AlbertLacambraBasil on 09.06.2017.
 */
public enum MarketRoleName {

    //Customer demand roles
    DEMAND_CREATOR(RoleType.CUSTOMER),
    CUSTOMER_SIGNER(RoleType.CUSTOMER),
    CLIENT_READ_RIGHT(RoleType.CUSTOMER),
    CUSTOMER_OFFER_TECHNICAL_APPROVAL(RoleType.CUSTOMER),
    CUSTOMER_OFFER_COMMERCIAL_APPROVAL(RoleType.CUSTOMER),

    //MAAS specific roles
    DEMAND_CONTROLLER(RoleType.CUSTOMER),
    DEMAND_APPROVAL(RoleType.CUSTOMER),

    //Supplier offer roles
    SUPPLIER_CONTROLLER(SUPPLIER),

    OFFER_CREATOR(SUPPLIER),
    SUPPLIER_SIGNER(SUPPLIER),
    SUPPLIER_READ_RIGHTS(SUPPLIER),
    SUPPLIER_PROJECT_MANAGER(RoleType.CUSTOMER),

    //Supplier offer roles
    SUPPLIER_OFFER_APPROVAL(SUPPLIER),


    PEER(RoleType.PEER),
    NONE(RoleType.NONE);

    RoleType roleType;

    MarketRoleName(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
