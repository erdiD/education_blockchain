package de.deutschebahn.ilv.app.user;

import de.deutschebahn.ilv.domain.User;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Created by AlbertLacambraBasil on 30.10.2017.
 */
@SessionScoped
public class LoggedUser implements Serializable {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogged() {
        return user != null;
    }

    @Override
    public String toString() {
        return "LoggedUser{" +
                "user=" + user +
                '}';
    }
}
