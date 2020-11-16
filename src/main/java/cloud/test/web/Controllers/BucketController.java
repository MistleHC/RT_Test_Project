package cloud.test.web.Controllers;


import cloud.test.web.Constants;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.DAO.AvroDao;
import cloud.test.web.Service.AvroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(Constants.BUCKET_URLS)
public class BucketController {

    private static final Logger logger = LoggerFactory.getLogger(BucketController.class);
    final private AvroService avroService;
    final private AvroDao avroDao;

    @Autowired
    public BucketController(AvroService avroService, AvroDao avroDao) {
        this.avroService = avroService;
        this.avroDao = avroDao;
    }

    @PostMapping("/change")
    public ResponseEntity<?> BucketNewFileEvent(@RequestBody String request) {
        DataValidator.isNotEmptyValidate(request, "Request body");
        logger.info("Change notification received");
        logger.info(request);
        avroService.downloadFile(request.substring(request.indexOf("\"objectId\":\"") + 12, request.lastIndexOf("\",\"payloadFormat\"")));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
