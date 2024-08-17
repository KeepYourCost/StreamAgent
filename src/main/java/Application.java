import infrastructure.singleton.SingletonRegistry;
import infrastructure.singleton.SingletonFactory;
import infrastructure.singleton.SingletonScanner;
import io.javalin.Javalin;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);
        SingletonRegistry.registerInstanceAsSingleton(Javalin.class, app);

        SingletonScanner.scanSingletonsFromRoot();


//        FileDataProducer fileDataProducer = new FileDataProducer(
//                FileSplitter.getInstance()
//        );
//        FileDataConsumer consumer = new FileDataConsumer();
//
//        consumer.consumeFileDataStream();
//        try {
//            String filePath = "D:\\StreamAgent\\src\\main\\resources\\sample\\SampleImg.png";
//            dataProducer.produceFileDataStream(filePath);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//"D:\\StreamAgent\\src\\main\\resources\\sample\\snowy-mountain-peak-starry-galaxy-majesty-generative-ai.jpg"




//        SpotController spotController = SingletonFactory.getInstance(SpotController.class);

        B b = SingletonFactory.getInstance(B.class);
        b.sout();
    }
}
