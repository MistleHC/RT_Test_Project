package cloud.test.web.EventHandlers;

import cloud.test.web.Controllers.DTO.FileData;
import cloud.test.web.DAO.AvroDao;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AvroBucketEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AvroBucketEventHandler.class);
    private static final String projectId = "rt-test-project-292414";
    private static final String subscriptionId = "processfiles";
    private final AvroDao avroDao;

    public AvroBucketEventHandler(AvroDao avroDao) {
        this.avroDao = avroDao;
        logger.info("Subscribtion initialized");
    }

    public void subscribeOnAvroBucketNewFiles() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            logger.info("Id: " + message.getMessageId());
            logger.info("Data: " + message.getData().toStringUtf8());
            Gson gson = new Gson();
            FileData fileData = gson.fromJson(message.getData().toStringUtf8(), FileData.class);
            if(fileData.getId() != null && fileData.getName() != null) {
                logger.info("RESULT: " + fileData.getId() + " " + fileData.getName());
                avroDao.downloadFile(fileData.getName());
            } else {
                logger.error("File data is null");
            }
            consumer.ack();
        };

        Subscriber subscriber = null;

        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            subscriber.startAsync().awaitRunning();
            logger.info("Listening for messages on - " + subscriptionName.toString());
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            subscriber.stopAsync();
            //subscribeOnAvroBucketNewFiles();
        }
    }
}
