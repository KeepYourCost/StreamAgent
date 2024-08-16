package controller;

import clients.consumer.FileDataConsumer;
import clients.consumer.FileInfo;
import clients.producer.DataProducer;
import util.FileBuffer;
import util.FileCombiner;

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
