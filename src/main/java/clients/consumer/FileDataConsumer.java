package clients.consumer;

import config.KafkaConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class FileDataConsumer {
    private static String PREVIOUS_SPOT_ID = "YTA0YjkwYWEzZDY0MTFlZj";
    //OGViZjY4ZmMzZDY0MTFlZm
    private static final long POLLING_DURATION = 100L; // Millis
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataConsumer.class);

    public FileDataConsumer() {
    }

    public FileInfo consumeFileDataStream() throws IllegalFormatFlagsException{
        FileBuffer buffer = new FileBuffer();
        String currentFilePath = "";

        try (final Consumer<String, byte[]> consumer = new KafkaConsumer<>(KafkaConfig.getConsumerProperties())) {
            consumer.subscribe(Arrays.asList(PREVIOUS_SPOT_ID));
            
            // 파티션 offset을 0으로 설정
            consumer.assignment().forEach(topicPartition -> consumer.seek(topicPartition, 0));
            consumer.poll(Duration.ZERO);

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(POLLING_DURATION));
                for (ConsumerRecord<String, byte[]> record : records) {
                    LOGGER.info("KEY: {}", record.key());

                    if (record.key().equals("PREAMBLE") && AmbleManager.isPreamble(record.value())) {
                        LOGGER.info("START");
                        continue;
                    }

                    if (record.key().equals("DONE") && AmbleManager.isPostamble(record.value()) && !currentFilePath.isEmpty()) {
                        LOGGER.info("DONE");
                        return null;
                    }

                    KeyRecord keyRecord = KeyManager.parseKey(record.key());
                    byte[] value = record.value();

                    String filePath = keyRecord.path();
                    int chunkIndex = keyRecord.index();
                    System.out.println(filePath);
                    System.out.println(chunkIndex);

                    if (!currentFilePath.isEmpty() && !Objects.equals(filePath, currentFilePath)) {
                        LOGGER.info("WRITE FILE path: {}", currentFilePath);
                        buffer.clearChunks();
                    }

                    buffer.addChunk(value);
                    currentFilePath = filePath;
                }
            }
        }
    }
}
