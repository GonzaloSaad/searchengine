package utn.frc.dlc.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Properties;

public class Environment {

    private static Properties properties;

    public static String getProperty(String propertyName) {
        Properties props = getProperties();
        return props.getProperty(propertyName);
    }

    public static String getPathOfWorkspace() {
        return System.getProperty("user.dir") + getProperty("dlc.searchcore.basepath");
    }

    private static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();

            try {
                properties.load(new FileInputStream(getFileForResource("application.properties")));
            } catch (Exception e) {
                throw new RuntimeException("No properties could be loaded.");
            }
        }
        return properties;
    }

    private static File getFileForResource(String resource) throws URISyntaxException {
        return new File(Environment.class.getClassLoader().getResource(resource).toURI());
    }

}
