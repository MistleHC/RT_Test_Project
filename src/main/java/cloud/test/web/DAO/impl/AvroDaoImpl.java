package cloud.test.web.DAO.impl;

import cloud.test.web.Controllers.HelloWorldController;
import cloud.test.web.DAO.AvroDao;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import example.gcp.Client;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Repository;
import com.google.cloud.storage.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;

@Repository
public class AvroDaoImpl implements AvroDao {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    @Autowired
    public AvroDaoImpl() {

    }
    @Override
    public void uploadClient(Client client) {
        String objectName = "client-" + String.valueOf(client.getId()) +".avro";
        File avroOutput = new File(objectName);
        try {
            DatumWriter<Client> clientDatumWriter = new SpecificDatumWriter<Client>(Client.class);
            DataFileWriter<Client> dataFileWriter = new DataFileWriter<Client>(clientDatumWriter);
            dataFileWriter.create(client.getSchema(), avroOutput);
            dataFileWriter.append(client);
            dataFileWriter.close();
            logger.info("DAO: File created");

            FileInputStream input = new FileInputStream(avroOutput);
            MultipartFile output = new MockMultipartFile("file",
                    avroOutput.getName(), "text/plain", IOUtils.toByteArray(input));

            uploadObjectToBucket(objectName, objectName, output);
        } catch (IOException e) {System.out.println("Error writing Avro");}
    }

    private void uploadObjectToBucket(String objectName, String filePath, MultipartFile file)  throws IOException{
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("RT Test Project-032a2604a3f9.json")).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of("avro_bucket-1", objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        logger.info("File " + filePath + " uploaded to bucket " + "avro_bucket-1" + " as " + objectName);
    }

}
