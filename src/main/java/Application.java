import infrastructure.singleton.SingletonScanner;
import spot.controller.SpotController;
import infrastructure.singleton.SingletonFactory;
import io.javalin.Javalin;

public class Application {
    public static void main(String[] args) {
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


        Javalin app = Javalin.create().start(8080);
        SingletonScanner.registerInstanceAsSingleton(Javalin.class, app);

        SingletonScanner.scanSingletonsFromRoot();


//        SpotController spotController = SingletonFactory.getInstance(SpotController.class);

        B b = SingletonFactory.getInstance(B.class);
        b.sout();
    }
}
