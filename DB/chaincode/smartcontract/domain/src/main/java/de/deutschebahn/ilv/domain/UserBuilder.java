package de.deutschebahn.ilv.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AlbertLacambraBasil on 10.10.2017.
 */
public final class UserBuilder implements CanBuild<User> {
    private Date dateCreated;
    private Date lastModified;
    private String id;
    private String firsName;
    private String lastName;
    private String userName;
    private String password;
    private List<MarketRoleName> marketRole = new ArrayList();
    private String organizationId;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder withDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public UserBuilder withLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public UserBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder withFirsName(String firsName) {
        this.firsName = firsName;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withMarketRole(List<MarketRoleName> marketRole) {
        this.marketRole = marketRole;
        return this;
    }

    public UserBuilder withOrganizationId(String organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public User build() {
        User user = new User();
        user.setDateCreated(dateCreated);
        user.setLastModified(lastModified);
        user.setId(id);
        user.setFirsName(firsName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setPassword(password);
        user.setMarketRole(marketRole);
        user.setOrganizationId(organizationId);
        return user;
    }
}
