package cloud.test.web.DAO;

import example.gcp.Client;
import example.gcp.ClientEx;

public interface AvroDao {
    public void uploadClient(ClientEx client);

    void downloadFile(String name);
}
