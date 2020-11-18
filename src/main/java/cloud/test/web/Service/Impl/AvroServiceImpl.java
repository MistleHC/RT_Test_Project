package cloud.test.web.Service.Impl;

import cloud.test.web.Controllers.DTO.DtoClient;
import cloud.test.web.Controllers.Validators.DataValidator;
import cloud.test.web.DAO.AvroDao;
import cloud.test.web.Service.AvroService;
import example.gcp.Client;
import example.gcp.ClientEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AvroServiceImpl implements AvroService {

    private static final Logger logger = LoggerFactory.getLogger(AvroServiceImpl.class);
    private final AvroDao avroDao;

    @Autowired
    public AvroServiceImpl(AvroDao avroDao) {
        this.avroDao = avroDao;
    }

    @Override
    public void generateAvro(DtoClient dtoClient) {
        ClientEx client = new ClientEx();
        client.setId(Math.abs(new Random().nextLong()));
        client.setName(dtoClient.getName());
        if(DataValidator.isNotEmptyCheck(dtoClient.getPhone())) {client.setPhone(dtoClient.getPhone());}
        if(DataValidator.isNotEmptyCheck(dtoClient.getAddress())) {client.setAddress(dtoClient.getAddress());}

        client.setVerified(dtoClient.isVerified());
        client.setBill(dtoClient.getBill());

        logger.info(client.toString());
        avroDao.uploadClient(client);
    }

    @Override
    public void downloadFile(String name) {
        avroDao.downloadFile(name);
    }
}
