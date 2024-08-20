package core.clients.consumer;

import config.KafkaConfig;
import core.clients.provider.TopicProvider;
import core.file.ByteBuffer;
import core.file.FileCombiner;
import core.file.FileWriter;
import core.util.*;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Singleton
public class FileDataConsumer {
    private final FileWriter fileWriter;
    private final FileCombiner fileCombiner;
    private static final long POLLING_DURATION = 100L; // Millis
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataConsumer.class);

    @Injection
    public FileDataConsumer(
            FileWriter fileWriter,
            FileCombiner fileCombiner
    ) {
        this.fileWriter = fileWriter;
        this.fileCombiner = fileCombiner;
    }

    public void consumeFile() throws IllegalFormatFlagsException {
        ByteBuffer buffer = new ByteBuffer();
        String currentFilePath = "";

        try (final Consumer<String, byte[]> consumer = createConsumer()) {
            // Topic 가져오기
            final String topic = TopicProvider.getConsumeTopic();
            consumer.subscribe(Arrays.asList(topic));
            setTopicPartitionZero(consumer);

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(POLLING_DURATION));
                for (ConsumerRecord<String, byte[]> record : records) {
                    LOGGER.info("KEY: {}", record.key());

                    if (isPreamble(record)) {
                        continue;
                    }

                    if (isDone(record, currentFilePath)) {
                        return;
                    }

                    KeyRecord keyRecord = KeyManager.parseKey(record.key());
                    byte[] value = record.value();
                    String filePath = keyRecord.path();

                    currentFilePath = writeIfNecessary(buffer, currentFilePath, filePath);

                    buffer.addChunk(value);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Kafka Consumer를 생성한다.
     * @return Kafka Consumer 객체
     */
    private Consumer<String, byte[]> createConsumer() {
        return new KafkaConsumer<>(KafkaConfig.getConsumerProperties());
    }

    /**
     * 파티션 offset을 0으로 설정
     * @param consumer
     */
    private void setTopicPartitionZero(Consumer<String, byte[]> consumer) {
        consumer.assignment().forEach(topicPartition -> consumer.seek(topicPartition, 0));
        consumer.poll(Duration.ZERO);
    }

    /**
     * PREAMBLE 메시지인지 확인한다.
     * @param record Kafka Consumer Record
     * @return PREAMBLE 메시지인 경우 true
     */
    private boolean isPreamble(ConsumerRecord<String, byte[]> record) {
        if (record.key().equals("PREAMBLE") && AmbleManager.isPreamble(record.value())) {
            LOGGER.info("START");
            return true;
        }
        return false;
    }

    /**
     * DONE 메시지인지 확인한다.
     * @param record Kafka Consumer Record
     * @param currentFilePath 현재 처리 중인 파일 경로
     * @return DONE 메시지인 경우 true
     */
    private boolean isDone(ConsumerRecord<String, byte[]> record, String currentFilePath) {
        if (record.key().equals("DONE") && AmbleManager.isPostamble(record.value()) && !currentFilePath.isEmpty()) {
            LOGGER.info("DONE");
            return true;
        }
        return false;
    }

    /**
     * 파일 경로가 변경될 때, 버퍼의 내용을 파일로 작성하고 경로를 갱신한다.
     * @param buffer ByteBuffer 객체
     * @param currentFilePath 현재 파일 경로
     * @param newFilePath 새로운 파일 경로
     * @return 갱신된 파일 경로
     * @throws IOException 파일 작성중 실패할 경우
     */
    private String writeIfNecessary(ByteBuffer buffer, String currentFilePath, String newFilePath) throws IOException {
        if (!currentFilePath.isEmpty() && !Objects.equals(newFilePath, currentFilePath)) {
            byte[] bytes = fileCombiner.combine(buffer);
            fileWriter.write(currentFilePath, bytes);
            buffer.clearChunks();
        }
        return newFilePath;
    }


}
