package spot.service;

import core.clients.provider.TopicProvider;
import infrastructure.factory.Singleton;

@Singleton
public class SpotService {
    private final TopicProvider topicProvider;

    public SpotService (
            TopicProvider topicProvider
    ) {
        this.topicProvider = topicProvider;
    }

    public void registerTopic(String currSpotId, String prevSpotId) {
        topicProvider.registerProduceTopic(currSpotId);
        topicProvider.registerConsumeTopic(prevSpotId);
    }
}
