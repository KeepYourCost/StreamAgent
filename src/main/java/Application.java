import infrastructure.singleton.SingletonRegistry;
import infrastructure.singleton.SingletonFactory;
import infrastructure.singleton.SingletonScanner;
import io.javalin.Javalin;
import spot.controller.SpotController;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(5050);
        SingletonRegistry.registerInstanceAsSingleton(Javalin.class, app);

        SingletonScanner.scanSingletonsFromRoot();

        SpotController spotController = SingletonFactory.getInstance(SpotController.class);
        spotController.handlerBackupData();
        spotController.handlerRecoverData();
        spotController.handleRegisterSpotIdRequest();

        spotController.readManifest();

//        TopicProvider topicProvider = SingletonFactory.getInstance(TopicProvider.class);
//        topicProvider.registerProduceTopic("TEST");
//        topicProvider.registerConsumeTopic("TEST");
//
//
//        FileDataProducer producer = SingletonFactory.getInstance(FileDataProducer.class);
//        producer.produceFileDataStream(
//                "C:\\CODE\\oss\\kyc\\kyc-agent\\src\\main\\resources\\sample\\src\\mysql-connector-j-8.4.0.jar",
//                "C:\\CODE\\oss\\kyc\\kyc-agent\\src\\main\\resources\\sample\\dest\\mysql-connector-j-8.4.0.jar"
//        );
//
//        System.out.println("produce 완료");
//
//        FileDataConsumer consumer = SingletonFactory.getInstance(FileDataConsumer.class);
//        consumer.consumeFile();

    }
}
