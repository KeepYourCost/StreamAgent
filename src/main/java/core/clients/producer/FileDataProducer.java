package core.clients.producer;

import config.KafkaConfig;
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
    private final FileSplitter fileSplitter;
    private static String CURRENT_SPOT_ID = "YTA0YjkwYWEzZDY0MTFlZj";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataProducer.class);

    @Injection
    public FileDataProducer(FileSplitter fileSplitter) {
        this.fileSplitter = fileSplitter;
    }

    public void produceFileDataStream(String src) {
        try {
            produce(src, src);
        } catch (IOException ex) {
            LOGGER.error("Failed to split data stream");
        }
    }

    public void produceFileDataStream(String src, String dest) {
        try {
            produce(src, dest);
        } catch (IOException ex) {
            LOGGER.error("Failed to split data stream");
        }
    }

    private void produce(String src, String dest) throws IOException {
        LOGGER.info("데이터 전송 준비");
        List<byte[]> dataStreams = fileSplitter.split(src).getChunks();
        LOGGER.info("데이터 분할 완료. Path = {}", src);

        try (final Producer<String, byte[]> producer = new KafkaProducer<>(KafkaConfig.getProducerProperties())) {
            // 프리앰블 전송
            sendPreamble(producer);

            for (int i = 0; i < dataStreams.size(); i++) {
                final String key = KeyManager.generateKey(dest, i);
                producer.send(
                        new ProducerRecord<>(
                                CURRENT_SPOT_ID,
                                key,
                                dataStreams.get(i)),
                        (event, ex) -> {
                            if (ex != null) {
                                ex.printStackTrace();
                                LOGGER.error(ex.getMessage());
                            }
                            else {
                                LOGGER.info("Produced event to topic '{}': key = {}", CURRENT_SPOT_ID, key);
                            }
                        }
                );
            }

            // 포스트앰블 전송
            sendPostamble(producer);
        }
        LOGGER.info("데이터 전송 완료");
    }

    private void sendPreamble(Producer<String, byte[]> producer) {
        byte[] preamble = AmbleManager.generatePreamble();
        producer.send(
                new ProducerRecord<>(
                        CURRENT_SPOT_ID,
                        "PREAMBLE",
                        preamble),
                (event, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                    else {
                        LOGGER.info("Produced preamble to topic '{}'", CURRENT_SPOT_ID);
                    }
                }
        );
    }

    private void sendPostamble(Producer<String, byte[]> producer) {
        byte[] postamble = AmbleManager.generatePostamble();
        producer.send(
                new ProducerRecord<>(
                        CURRENT_SPOT_ID,
                        "DONE",
                        postamble),
                (event, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                    else {
                        LOGGER.info("Produced postamble to topic '{}'", CURRENT_SPOT_ID);
                    }
                }
        );
    }
}