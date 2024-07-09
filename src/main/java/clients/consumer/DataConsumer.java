package clients.consumer;

import clients.producer.DataProducer;
import config.KafkaConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileCombiner;

import java.time.Duration;
import java.util.Arrays;

public class DataConsumer {
    private final FileCombiner fileCombiner;
    private static String PREVIOUS_SPOT_ID = "OGViZjY4ZmMzZDY0MTFlZm";
    private static String CURRENT_SPOT_ID = "YTA0YjkwYWEzZDY0MTFlZj";
    private static final String KEY_FORMAT = "%s::%d";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataConsumer.class);

    public DataConsumer(FileCombiner fileCombiner) {
        this.fileCombiner = fileCombiner;
    }

    public void consumeByteStream() {
        try (final Consumer<String, byte[]> consumer = new KafkaConsumer<>(KafkaConfig.getConsumerProperties())){
            consumer.subscribe(Arrays.asList(PREVIOUS_SPOT_ID));
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, byte[]> record : records) {
                    String key = record.key();
                    byte[] value = record.value();
                }
            }
        }

    }
}
