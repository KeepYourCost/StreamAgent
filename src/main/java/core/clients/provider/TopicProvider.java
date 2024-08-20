package core.clients.provider;

import infrastructure.singleton.Injection;
import infrastructure.singleton.Singleton;

@Singleton
public class TopicProvider {

    @Injection
    public TopicProvider() {
    }

    private String produceTopic = null;
    private String consumeTopic = null;

    public void registerProduceTopic(String produceTopic) {
        this.produceTopic = produceTopic;
    }

    public void registerConsumeTopic(String consumeTopic) {
        this.consumeTopic = consumeTopic;
    }

    public String getProduceTopic() throws IllegalCallerException{
        if (produceTopic == null) {
            throw new IllegalCallerException("Produce Topic이 등록되지 않음");
        }
        return produceTopic;
    }

    public String getConsumeTopic() throws IllegalCallerException {
        if (consumeTopic == null) {
            throw new IllegalCallerException("Consume Topic이 등록되지 않음");
        }
        return consumeTopic;
    }
}
