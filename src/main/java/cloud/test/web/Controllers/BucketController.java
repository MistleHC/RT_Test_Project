package cloud.test.web.Controllers;


import cloud.test.web.Constants;
import cloud.test.web.Controllers.DTO.DtoBucketNotification;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.DAO.AvroDao;
import cloud.test.web.Service.AvroService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> BucketNewFileEvent(@RequestBody DtoBucketNotification dtoBucketNotification) {

        DtoBucketNotification.Message message = dtoBucketNotification.getMessage();
        if(message == null) {
            String msg = "Bad Request: invalid Pub/Sub message format";
            logger.error(msg);
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }

        String data = message.getData();
        DataValidator.isNotEmptyValidate(data, "request data");
        data = new String(Base64.getDecoder().decode(data));
        logger.info("DATA: " + data);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(data);
        Object name = jsonObject.get("name");
        avroService.downloadFile((String) name);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
