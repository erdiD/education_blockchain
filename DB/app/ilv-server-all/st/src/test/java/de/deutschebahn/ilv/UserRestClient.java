package de.deutschebahn.ilv;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by AlbertLacambraBasil on 04.08.2017.
 */
public class UserRestClient {
    String username;
    String endPoint;
    Client client;
    WebTarget webTarget;

    public UserRestClient(String username, String endPoint) {
        this.username = username;
        this.endPoint = endPoint;
    }

    public void connect() {
        client = ClientBuilder.newClient().register(MultiPartFeature.class).register(new RequestResponseFilter());
        webTarget = client.target(endPoint);
        TestUtils.loginUser(webTarget, username);
    }

    public String getUsername() {
        return username;
    }

    public Client getClient() {
        return client;
    }

    public WebTarget getWebTarget() {
        return webTarget;
    }

    public void close() {

        if (client != null) {
            client.close();
        }
    }
}
