package de.deutschebahn.ilv.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigProducer {

    private static final Logger logger = Logger.getLogger(ConfigProducer.class.getName());

    @Produces
    @FabricProperties
    @ApplicationScoped
    public Properties getFabricProperties(@Environment String environment) {

        InputStream inputStream = ConfigProducer.class.getClassLoader().getResourceAsStream(environment + "/config.properties");
        if (inputStream == null) {
            throw new RuntimeException("Properties not found=" + environment + "/config.properties");
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("[getFabricProperties] Properties loaded=" + properties);

        return properties;
    }

    @Produces
    @Environment
    public String getEnvironment() {
        String environment = System.getenv("ENVIRONMENT");
        if (environment == null) {
            environment = "default";
        }
        logger.info("[getFabricProperties] using environment=" + environment);
        return environment;
    }
}
