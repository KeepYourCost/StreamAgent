package spot.controller;

import common.vo.Message;
import infrastructure.singleton.Singleton;
import io.javalin.Javalin;
import spot.controller.vo.ReqRegisterSpotId;
import spot.service.SpotService;

@Singleton
public class SpotController {
    private final Javalin app;
    private final SpotService spotService;

    public SpotController(
            Javalin app,
            SpotService spotService
    ) {
        this.app = app;
        this.spotService = spotService;
    }

    public void registerSpotId () {
        app.post("/spot-id", ctx -> {
            ReqRegisterSpotId req = ctx.bodyAsClass(ReqRegisterSpotId.class);

            spotService.registerTopic(req.currSpotId(), req.prevSpotId());

            Message message = Message.of(200, "OK");

            ctx.status(message.statusCode());
            ctx.json(message);
        });
    }
}
