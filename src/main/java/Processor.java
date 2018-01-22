import config.Configuration;
import log.CloudLogger;
import utils.ThreadSleep;

public class  Processor {


    public static void main(String[] args) {
        final Configuration config = new Configuration();
        ActivityPoller poller = new ActivityPoller(config, new CloudLogger(config));
        while (true) {
            poller.poll();
            ThreadSleep.sleep(1000);
        }

    }
}
