import clients.producer.DataProducer;
import util.FileSplitter;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        DataProducer dataProducer = new DataProducer(
                FileSplitter.getInstance()
        );

        try {
            dataProducer.sendDataStreams("C:/CODE/oss/kyc/kyc-agent/src/main/resources/sample/SampleImg.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
