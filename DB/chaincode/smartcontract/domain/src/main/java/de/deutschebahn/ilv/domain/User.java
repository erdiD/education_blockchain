package de.deutschebahn.ilv.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Persistable {


    private static final String prefix = "User.";
    public static final String selectAll = prefix + "all";
    public static final String selectByUserName = prefix + "byUsername";
    public static final String selectById = prefix + "byId";

    private Date dateCreated;
    private Date lastModified;
    private String id;
    private String firsName;
    private String lastName;
    private String userName;
    private String password;
    private List<MarketRoleName> marketRole = new ArrayList();
    private String organizationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirsName() {
        return firsName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);

    }

    public List<MarketRoleName> getMarketRole() {
        return marketRole;
    }

    public void setMarketRole(List<MarketRoleName> marketRole) {
        this.marketRole = marketRole;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", marketRole=" + marketRole +
                ", organizationId='" + organizationId + '\'' +
                '}';
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
