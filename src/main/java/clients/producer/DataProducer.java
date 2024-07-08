package clients.producer;

import config.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileSplitter;

import java.io.IOException;
import java.util.List;

public class DataProducer {
    private final FileSplitter fileSplitter;
    private static String PREVIOUS_SPOT_ID = "OGViZjY4ZmMzZDY0MTFlZm";
    private static String CURRENT_SPOT_ID = "YTA0YjkwYWEzZDY0MTFlZj";
    private static final String KEY_FORMAT = "%s::%d";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataProducer.class);

    public DataProducer(FileSplitter fileSplitter) {
        this.fileSplitter = fileSplitter;
    }

    public void sendDataStreams(String filePath) throws IOException {
        LOGGER.info("데이터 전송 준비");
        List<byte[]> dataStreams = fileSplitter.splitFile(filePath).getChunks();
        LOGGER.info("데이터 분할 완료. Path = {}", filePath);

        try (final Producer<String, byte[]> producer = new KafkaProducer<>(ProducerConfig.getInstance())) {
            for (int i = 0; i < dataStreams.size(); i++) {
                final String key = String.format(KEY_FORMAT, filePath, i);
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
        }
        LOGGER.info("데이터 전송 완료");
    }
}
