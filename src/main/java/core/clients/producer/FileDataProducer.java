package core.clients.producer;

import config.KafkaConfig;
import core.clients.provider.TopicProvider;
import core.file.FileReader;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import core.util.AmbleManager;
import core.file.FileSplitter;
import core.util.KeyManager;

import java.io.IOException;
import java.util.List;

@Singleton
public class FileDataProducer {
    private final FileReader fileReader;
    private final FileSplitter fileSplitter;
    private final TopicProvider topicProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataProducer.class);

    @Injection
    public FileDataProducer(
            FileReader fileReader,
            FileSplitter fileSplitter,
            TopicProvider topicProvider
    ) {
        this.fileReader = fileReader;
        this.fileSplitter = fileSplitter;
        this.topicProvider = topicProvider;
    }

    public void produceFileDataStream(String src) {
        try {
            produceFile(src, src);
        } catch (IOException | IllegalCallerException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public void produceFileDataStream(String src, String dest) {
        try {
            produceFile(src, dest);
        } catch (IOException | IllegalCallerException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public void sendStartSignal() {
        final String topic = topicProvider.getProduceTopic();

        try (final Producer<String, byte[]> producer = new KafkaProducer<>(KafkaConfig.getProducerProperties())) {
            ProducerRecord preRecord = new ProducerRecord(topic, "PREAMBLE", AmbleManager.generatePreamble());
            sendMessage(producer, preRecord);
        }
    }

    public void sendEndSignal() {
        final String topic = topicProvider.getProduceTopic();

        try (final Producer<String, byte[]> producer = new KafkaProducer<>(KafkaConfig.getProducerProperties())) {
            ProducerRecord postRecord = new ProducerRecord(topic, "DONE", AmbleManager.generatePostamble());
            sendMessage(producer, postRecord);
        }
    }

    private void produceFile(String srcPath, String destPath) throws IOException, IllegalCallerException {
        // File 읽기
        byte[] fileStream = fileReader.read(srcPath);
        List<byte[]> dataStreams = fileSplitter.split(fileStream).getChunks();
//        LOGGER.info("Successfully read the file. Source Path = {}", srcPath);

        // Topic 가져오기
        final String topic = topicProvider.getProduceTopic();

        // File 전송하기
        try (final Producer<String, byte[]> producer = new KafkaProducer<>(KafkaConfig.getProducerProperties())) {
            // 직렬 produce 방식
            // 성능 개선이 필요할 경우 Stream API와 parallel 도입 가능
            for (int i = 0; i < dataStreams.size(); i++) {
                final String key = KeyManager.generateKey(destPath, i);
                final byte[] value = dataStreams.get(i);
                ProducerRecord record = new ProducerRecord(topic, key, value);

                sendMessage(producer, record);
            }
        }

//        LOGGER.info("Successfully produced file data. File = {}", srcPath);
    }

    private void sendMessage(Producer<String, byte[]> producer, ProducerRecord record) {
        producer.send(
                record,
                (event, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        LOGGER.error(ex.getMessage());
                    } else {
                        LOGGER.debug("Produced event to topic '{}': key = {}", record.topic(), record.key());
                    }
                }
        );
    }
}