package clients.producer;

import config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AmbleManager;
import util.FileSplitter;
import util.KeyManager;

import java.io.IOException;
import java.util.List;

public class DataProducer {
    private final FileSplitter fileSplitter;
    private static String CURRENT_SPOT_ID = "YTA0YjkwYWEzZDY0MTFlZj";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProducer.class);

    public DataProducer(FileSplitter fileSplitter) {
        this.fileSplitter = fileSplitter;
    }

    public void produceFileDataStream(String filePath) throws IOException {
        LOGGER.info("데이터 전송 준비");
        List<byte[]> dataStreams = fileSplitter.splitFile(filePath).getChunks();
        LOGGER.info("데이터 분할 완료. Path = {}", filePath);

        try (final Producer<String, byte[]> producer = new KafkaProducer<>(KafkaConfig.getProducerProperties())) {
            // 프리앰블 전송
            sendPreamble(producer);

            for (int i = 0; i < dataStreams.size(); i++) {
                final String key = KeyManager.generateKey(filePath, i);
                producer.send(
                        new ProducerRecord<>(
                                CURRENT_SPOT_ID,
                                key,
                                dataStreams.get(i)),
                        (event, ex) -> {
                            if (ex != null) {
                                ex.printStackTrace();
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