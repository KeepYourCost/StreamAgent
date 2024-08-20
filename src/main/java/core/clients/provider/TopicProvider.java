package core.clients.provider;

public class TopicProvider {
    public TopicProvider() {
    }

    private static String produceTopic = null;
    private static String consumeTopic = null;

    public void registerProduceTopic(String produceTopic) {
        this.produceTopic = produceTopic;
    }

    public void registerConsumeTopic(String consumeTopic) {
        this.consumeTopic = consumeTopic;
    }

    public static String getProduceTopic() throws IllegalCallerException{
        if (produceTopic == null) {
            throw new IllegalCallerException("Produce Topic이 등록되지 않음");
        }
        return produceTopic;
    }

    public static String getConsumeTopic() throws IllegalCallerException {
        if (consumeTopic == null) {
            throw new IllegalCallerException("Consume Topic이 등록되지 않음");
        }
        return consumeTopic;
    }
}
