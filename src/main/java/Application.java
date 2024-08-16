import clients.consumer.FileDataConsumer;
import clients.producer.DataProducer;
import util.FileSplitter;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        DataProducer dataProducer = new DataProducer(
                FileSplitter.getInstance()
        );
        FileDataConsumer consumer = new FileDataConsumer();

        try {
//        consumer.consumeFileDataStream();
            dataProducer.produceFileDataStream("D:\\StreamAgent\\src\\main\\resources\\sample\\SampleImg.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }
}
