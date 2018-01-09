package de.deutschebahn.ilv.domain;

public class MarketRole {

    private User user;
    private Organization organization;
    private MarketRoleName roleName;
    private String userId;
    private String organizationId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public MarketRoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(MarketRoleName roleName) {
        this.roleName = roleName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

        MarketRole role = (MarketRole) o;

        if (!user.equals(role.user)) return false;
        if (!organization.equals(role.organization)) return false;
        return roleName == role.roleName;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + organization.hashCode();
        result = 31 * result + roleName.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "MarketRole{" +
                ", roleName=" + roleName +
                ", user=" + user +
                ", organization=" + organization +
                '}';
    }
}

