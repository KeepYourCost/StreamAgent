package spot.service;

import core.clients.provider.TopicProvider;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

@Singleton
public class SpotService {
    private final TopicProvider topicProvider;

    @Injection
    public SpotService (
            TopicProvider topicProvider
    ) {
        this.topicProvider = topicProvider;
    }

    public void registerTopic(String currSpotId, String prevSpotId) {
        topicProvider.registerProduceTopic(currSpotId);
        topicProvider.registerConsumeTopic(prevSpotId);
    }

    public String fetchCurrentSpotId() {

    }
}
