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
    }
}
