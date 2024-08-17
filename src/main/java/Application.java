import core.clients.provider.TopicProvider;
import core.clients.consumer.FileDataConsumer;
import core.clients.producer.DataProducer;
import spot.controller.SpotController;
import infrastructure.factory.SingletonFactory;
import io.javalin.Javalin;
import spot.service.SpotService;
import core.util.FileSplitter;

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


        Javalin app = Javalin.create().start(8080);
        new SpotController(
                app,
                SingletonFactory.getInstance(SpotService.class,
                        SingletonFactory.getInstance(TopicProvider.class))
        );
    }
}
