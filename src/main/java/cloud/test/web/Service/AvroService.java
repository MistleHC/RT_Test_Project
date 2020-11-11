package cloud.test.web.Service;

import cloud.test.web.Controllers.DTO.DtoClient;
import example.gcp.Client;

public interface AvroService {
    void generateAvro(DtoClient dtoClient);

    void downloadFile(String name);
}
