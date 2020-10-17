package cloud.test.web.Service.Impl;

import cloud.test.web.Controllers.DTO.DtoClient;
import cloud.test.web.Controllers.HelloWorldController;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.DAO.AvroDao;
import cloud.test.web.DAO.impl.AvroDaoImpl;
import cloud.test.web.Service.AvroService;
import example.gcp.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AvroServiceImpl implements AvroService {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
    private final AvroDao avroDao;

    @Autowired
    public AvroServiceImpl(AvroDao avroDao) {
        this.avroDao = avroDao;
    }

    @Override
    public void generateAvro(DtoClient dtoClient) {
        Client client = new Client();
        client.setId(new Random().nextLong());
        client.setName(dtoClient.getName());
        if(DataValidator.isNotEmptyCheck(dtoClient.getPhone())) {client.setPhone(dtoClient.getPhone());}
        if(DataValidator.isNotEmptyCheck(dtoClient.getAddress())) {client.setAddress(dtoClient.getAddress());}

        logger.info("Service LOG");
        logger.info(client.toString());

        avroDao.uploadClient(client);
    }
}
