package spot.service;

import core.clients.provider.TopicProvider;
import infrastructure.http.Fetcher;
import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;
import spot.service.vo.TargetFile;

import java.io.IOException;
import java.util.List;

@Singleton
public class SpotService {
    private final TopicProvider topicProvider;
    private final ManifestReader manifestReader;

    @Injection
    public SpotService (
            TopicProvider topicProvider,
            ManifestReader manifestReader
    ) {
        this.topicProvider = topicProvider;
        this.manifestReader = manifestReader;
    }

    public void registerTopic(String currSpotId, String prevSpotId) {
        topicProvider.registerProduceTopic(currSpotId);
        topicProvider.registerConsumeTopic(prevSpotId);
    }
    public void registerProduceTopic(String currSpotId) {
        topicProvider.registerProduceTopic(currSpotId);
    }

    public void registerConsumeTopic(String prevSpotId) {
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

    public List<TargetFile> readManifest() throws IOException {
        List<TargetFile> targetFiles = manifestReader.read();
        System.out.println(targetFiles);

        return targetFiles;
    }
}
