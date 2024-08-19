package spot.service;

import core.clients.provider.TopicProvider;
import infrastructure.http.Fetcher;
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

    public String fetchCurrSpotId() {
        Fetcher.Respone respone = Fetcher.method("GET").path("/spot/current").mime("JSON").send();

        return respone.data().get("spotId").toString();
    }

    public String fetchPrevSpotId(String currId) {
        Fetcher.Respone respone = Fetcher.method("GET").path("/spot/prev/"+currId).mime("JSON").send();

        return respone.data().get("spotId").toString();
    }
}
