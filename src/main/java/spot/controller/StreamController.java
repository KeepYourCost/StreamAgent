package spot.controller;

import core.clients.consumer.FileDataConsumer;
import core.clients.producer.FileDataProducer;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

@Singleton
public class StreamController {
    private final FileDataConsumer consumer;
    public final FileDataProducer producer;

    @Injection
    public StreamController (
            FileDataConsumer consumer,
            FileDataProducer producer
    ) {
        this.consumer = consumer;
        this.producer = producer;
    }
    private void importData() {
    }
}
