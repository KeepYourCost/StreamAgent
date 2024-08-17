package spot.controller;

import core.clients.consumer.FileDataConsumer;
import core.clients.consumer.FileInfo;
import core.clients.producer.DataProducer;
import core.util.FileCombiner;

import java.util.IllegalFormatFlagsException;

public class StreamController {
    private final FileDataConsumer consumer;
    public final DataProducer producer;
    private final FileCombiner fileCombiner;
    public StreamController (
            FileDataConsumer consumer,
            DataProducer producer,
            FileCombiner fileCombiner
    ) {
        this.consumer = consumer;
        this.producer = producer;
        this.fileCombiner = fileCombiner;
    }
    private void importData() {
        // path에 대한 byteStream 읽기
        try {
            boolean isDone = false;
            while (!isDone) {
                FileInfo fileInfo = consumer.consumeFileDataStream();

            }

        } catch (IllegalFormatFlagsException ex) {
            ex.printStackTrace();
        }


    }
}
