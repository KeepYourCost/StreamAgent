import core.clients.consumer.FileDataConsumer;
import core.clients.producer.FileDataProducer;
import infrastructure.singleton.SingletonRegistry;
import infrastructure.singleton.SingletonFactory;
import infrastructure.singleton.SingletonScanner;
import io.javalin.Javalin;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);
        SingletonRegistry.registerInstanceAsSingleton(Javalin.class, app);

        SingletonScanner.scanSingletonsFromRoot();

        FileDataProducer producer = SingletonFactory.getInstance(FileDataProducer.class);
        producer.produceFileDataStream(
                "D:\\StreamAgent\\src\\main\\resources\\sample\\src\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.jpg",
                "D:\\StreamAgent\\src\\main\\resources\\sample\\dest\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.jpg"
        );

        System.out.println("produce 완료");

        FileDataConsumer consumer = SingletonFactory.getInstance(FileDataConsumer.class);
        consumer.consumeFile();

    }
}
