package de.deutschebahn.ilv.app;

import de.deutschebahn.ilv.domain.BusinessObject;

import java.util.function.Predicate;

/**
 * Created by alacambra on 04.06.17.
 */
public class Policy<T extends BusinessObject> {
    private String name;
    Predicate<T> predicate;

    public Policy(String name, Predicate<T> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }
}
