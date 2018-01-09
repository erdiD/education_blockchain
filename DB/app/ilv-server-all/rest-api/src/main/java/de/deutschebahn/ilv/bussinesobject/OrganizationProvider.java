package de.deutschebahn.ilv.bussinesobject;

import de.deutschebahn.ilv.domain.Organization;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AlbertLacambraBasil on 18.10.2017.
 */
@ApplicationScoped
public class OrganizationProvider {

    private final Map<String, Organization> organizations;
    //TODO: inject using CDI
    UserDataConverter userDataConverter = new UserDataConverter();

    public OrganizationProvider() {
        this.organizations = new ConcurrentHashMap<>();
        Organization dbs = new Organization();
        dbs.setId("dbs_id");
        dbs.setName("DB Systel");
        organizations.put(dbs.getId(), dbs);

        Organization dbe = new Organization();
        dbe.setId("dbe_id");
        dbe.setName("DB Energy");
        organizations.put(dbe.getId(), dbe);
    }

    public Organization getOrganization(String id) {
        return organizations.get(id);
    }
}
