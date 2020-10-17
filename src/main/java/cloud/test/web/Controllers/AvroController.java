package cloud.test.web.Controllers;

import cloud.test.web.Constants;
import cloud.test.web.Controllers.DTO.DtoClient;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.Service.AvroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(Constants.AVRO_URLS)
public class AvroController {
    final private AvroService avroService;

    @Autowired
    public AvroController(AvroService avroService) {
        this.avroService = avroService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAvro(@RequestBody DtoClient dtoClient) {
        DataValidator.isNotEmptyValidate(dtoClient.getName(), "client name");
        avroService.generateAvro(dtoClient);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
