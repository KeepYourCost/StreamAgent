package spot.controller;

import common.vo.Message;
import core.clients.provider.TopicProvider;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import io.javalin.Javalin;
import spot.controller.vo.ReqRegisterSpotId;
import spot.service.SpotService;

@Singleton
public class SpotController {
    private final Javalin app;
    private final SpotService spotService;

    @Injection
    public SpotController(
            Javalin app,
            SpotService spotService
    ) {
        this.app = app;
        this.spotService = spotService;
    }

    public void handleRegisterSpotIdRequest() {
        app.post("/spot-id", ctx -> {
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
        app.post("/read/:prevId", ctx -> {
            String prevId = ctx.pathParam("prevId");
            spotService.registerConsumeTopic(prevId);

            Message message = Message.of(200, "OK");


            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }

    public void handlerBackupData() {
        app.post("/write/:currId", ctx -> {
            String currId = ctx.pathParam("currId");
            spotService.registerProduceTopic(currId);

            Message message = Message.of(200, "OK");

            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }
}
