package config;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerConfig {
    public static Properties getInstance() {
        return properties;
    }
    private static final Properties properties = new Properties() {{
        // Kafka Broker가 Listen 하는 URL
        put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");

        // Kafka Key, Value 직렬화 class 설정
        put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        put(VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        // Kafka에 성공적으로 저장될 시 응답 수신 방식
        put(ACKS_CONFIG, "all");
    }};
}
