package de.deutschebahn.ilv.bussinesobject;


import de.deutschebahn.ilv.domain.MarketRoleName;

/**
 * Created by alacambra on 05.06.17.
 */
public class RoleInteractionPair<T> {

    private T stateInteraction;
    private MarketRoleName role;

    public RoleInteractionPair(T stateInteraction, MarketRoleName role) {
        this.stateInteraction = stateInteraction;
        this.role = role;
    }

    public T getStateInteraction() {
        return stateInteraction;
    }

    public MarketRoleName getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleInteractionPair that = (RoleInteractionPair) o;

        if (stateInteraction != that.stateInteraction) return false;
        return role == that.role;

    }

    @Override
    public int hashCode() {
        int result = stateInteraction.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }
}
