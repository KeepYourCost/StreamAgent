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

        consumer.consumeFileDataStream();
//        try {
//            String filePath = "D:\\StreamAgent\\src\\main\\resources\\sample\\SampleImg.png";
//            dataProducer.produceFileDataStream(filePath);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//"D:\\StreamAgent\\src\\main\\resources\\sample\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.jpg"


    }
}
