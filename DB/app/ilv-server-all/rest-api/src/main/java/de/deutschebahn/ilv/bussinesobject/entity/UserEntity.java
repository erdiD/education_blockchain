package de.deutschebahn.ilv.bussinesobject.entity;

import de.deutschebahn.ilv.domain.User;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by AlbertLacambraBasil on 20.10.2017.
 */
@Entity
public class UserEntity {

    @Id
    private String id;
    private String firsName;
    private String lastName;
    private String userName;
    private String organizationId;

    public UserEntity fromUser(User user) {
        setId(user.getId());
        setFirsName(user.getFirsName());
        setLastName(user.getLastName());
        setUserName(user.getUserName());
        setOrganizationId(user.getOrganizationId());
        return this;
    }

    public User toDomainUser() {
        User user = new User();
        user.setId(getId());
        user.setFirsName(getFirsName());
        user.setLastName(getLastName());
        user.setUserName(getUserName());
        user.setOrganizationId(getOrganizationId());

        return user;
    }

    public String getFirsName() {
        return firsName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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
        this.userName = userName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", firsName='" + firsName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", organizationId='" + organizationId + '\'' +
                '}';
    }
}
