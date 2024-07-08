package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class KafkaConfig {
    private static final String PRODUCER_PROPERTIES_FILE = "producer.properties";
    private static final String CONSUMER_PROPERTIES_FILE = "consumer.properties";
    private static final Properties producerProperties = loadProperties(PRODUCER_PROPERTIES_FILE);
    private static final Properties consumerProperties = loadProperties(CONSUMER_PROPERTIES_FILE);

    private static Properties loadProperties(String filePath) {
        Properties properties = new Properties();
        try (InputStream input = KafkaConfig.class.getClassLoader().getResourceAsStream(filePath)) {
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

    public static Properties getProducerProperties() {
        return producerProperties;
    }

    public static Properties getConsumerProperties() {
        return consumerProperties;
    }
}
