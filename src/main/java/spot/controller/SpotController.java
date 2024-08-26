package spot.controller;

import common.vo.Message;
import core.clients.consumer.FileDataConsumer;
import core.clients.producer.FileDataProducer;
import core.clients.provider.TopicProvider;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spot.controller.vo.ReqRegisterSpotId;
import spot.service.ManifestReader;
import spot.service.SpotService;
import spot.service.vo.TargetFile;

import java.io.IOException;
import java.util.List;

@Singleton
public class SpotController {
    private final Javalin app;
    private final SpotService spotService;
    private final FileDataConsumer consumer;
    private final FileDataProducer producer;
    private final ManifestReader manifestReader;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotController.class);

    @Injection
    public SpotController(
            Javalin app,
            SpotService spotService,
            FileDataConsumer consumer,
            FileDataProducer producer,
            ManifestReader manifestReader
    ) {
        this.app = app;
        this.spotService = spotService;
        this.consumer = consumer;
        this.producer = producer;
        this.manifestReader = manifestReader;

    }

    public void handleRegisterSpotIdRequest() {
        app.post("/spot-id", ctx -> {
            LOGGER.info("LISTEN `POST /spot-id`");
            ReqRegisterSpotId req = ctx.bodyAsClass(ReqRegisterSpotId.class);

            spotService.registerTopic(req.currSpotId(), req.prevSpotId());

            Message message = Message.of(200, "OK");

            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }

    public void registerSpotId() {
        String currSpotId = spotService.fetchCurrSpotId();
        String prevSpotId = spotService.fetchPrevSpotId(currSpotId);

        spotService.registerTopic(currSpotId, prevSpotId);
    }


    public void handlerRecoverData() {
        app.post("/read/{prevId}", ctx -> {
            LOGGER.info("LISTEN `POST /read/{prevId}`");

            String prevId = ctx.pathParam("prevId");
            spotService.registerConsumeTopic(prevId);

            //data consume 해서 읽어오기
            importData();

            Message message = Message.of(200, "OK");
            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }

    public void handlerBackupData() {
        app.post("/write/{currId}", ctx -> {
            LOGGER.info("LISTEN `POST /write/{currId}`");

            String currId = ctx.pathParam("currId");
            spotService.registerProduceTopic(currId);

            //data produce 해서 내보내기
            exportData();

            Message message = Message.of(200, "OK");

            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }

    public void readManifest() {
        try {
            spotService.readManifest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportData() {
        try {
            List<TargetFile> targetFiles = manifestReader.read();
            producer.sendStartSignal();

            for (TargetFile targetFile : targetFiles) {
                if (targetFile.type() == TargetFile.FileType.DIR) {
                    continue;
                }
                producer.produceFileDataStream(targetFile.path());
            }

            producer.sendEndSignal();
        } catch (IOException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
        }
    }

    public void importData() {
        consumer.consumeFile();
    }
}
