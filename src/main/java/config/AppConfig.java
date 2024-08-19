package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
    private static final Properties applicationProperties = loadProperties(APPLICATION_PROPERTIES_FILE);

    private static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                System.out.println("Unable to find " + filePath);
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    public static Properties getApplicationProperties() {
        return applicationProperties;
    }

}
