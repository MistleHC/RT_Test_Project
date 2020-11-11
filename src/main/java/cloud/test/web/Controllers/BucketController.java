package cloud.test.web.Controllers;


import cloud.test.web.Constants;
import cloud.test.web.Controllers.DTO.DtoBucketNotification;
import cloud.test.web.Controllers.DTO.DtoClient;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.DAO.AvroDao;
import cloud.test.web.Service.AvroService;
import cloud.test.web.Service.Impl.AvroServiceImpl;
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
    public ResponseEntity<?> generateAvro(@RequestHeader("X-Goog-Resource-State") String resourceState, @RequestBody DtoBucketNotification dtoBucketNotification) {
        DataValidator.isNotEmptyValidate(resourceState, "resource state");
        DataValidator.isNotEmptyValidate(dtoBucketNotification.getBucket(), "Bucket name");
        DataValidator.isNotEmptyValidate(dtoBucketNotification.getName(), "Object name");

        logger.info("Input file request: Name - " + dtoBucketNotification.getName() + ", Bucket - " + dtoBucketNotification.getBucket() + ", State - " + resourceState);
        if(resourceState.equals("exists")) {
            avroService.downloadFile(dtoBucketNotification.getName());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
