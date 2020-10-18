package cloud.test.web.Schedulers;

import cloud.test.web.DAO.AvroDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AvroScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AvroScheduler.class);

    private final AvroDao avroDao;

    @Autowired
    public AvroScheduler(AvroDao avroDao) {
        this.avroDao = avroDao;
    }

    @Scheduled(fixedRate = 20000)
    public void checkForNewFiles() {
        avroDao.checkNewFiles();
        logger.info(Thread.currentThread().getName()+" file check was executed.");
    }

}
