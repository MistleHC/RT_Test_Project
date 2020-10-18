package cloud.test.web.DAO.impl;

import com.google.cloud.bigquery.*;
import example.gcp.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
//GCP
import cloud.test.web.Controllers.HelloWorldController;
import cloud.test.web.DAO.AvroDao;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.cloud.storage.*;
//Avro
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.compress.utils.IOUtils;
//Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
//IO
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
//BigQuery


@Repository
public class AvroDaoImpl implements AvroDao {

    private static final Logger logger = LoggerFactory.getLogger(AvroDaoImpl.class);
    private static Storage storage = StorageOptions.getDefaultInstance().getService();
    private Environment env;

    @Autowired
    public AvroDaoImpl(Environment env) {
        this.env = env;
        authorize();
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

            FileInputStream input = new FileInputStream(avroOutput);
            MultipartFile output = new MockMultipartFile("file",
                    avroOutput.getName(), "text/plain", IOUtils.toByteArray(input));

            uploadObjectToBucket(objectName, objectName, output);
        } catch (IOException e) {System.out.println("Error writing Avro");}
    }

    public void checkNewFiles() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json")).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            Bucket bucket = storage.get("avro_bucket-1");
            Page<Blob> blobs = bucket.list();
            for (Blob blob : blobs.iterateAll()) {
                logger.info("Blob: " + blob.getName());
                blob.downloadTo(Paths.get(blob.getName()));
                avroDeserializeAndQuery(blob.getName());
                storage.delete("avro_bucket-1", blob.getName());
                logger.info("Object " + blob.getName() + "was deleted from the bucket avro_bucket-1");
            }
        } catch (IOException e) {System.out.println("Error while checking files");}
    }

    private void uploadObjectToBucket(String objectName, String filePath, MultipartFile file)  throws IOException{
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json")).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        BlobId blobId = BlobId.of("avro_bucket-1", objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        logger.info("File " + filePath + " uploaded to bucket " + "avro_bucket-1" + " as " + objectName);
    }

    private void avroDeserializeAndQuery(String fileName) {
        try{
            File file = new File(fileName);
            DatumReader<Client> clientDatumReader = new SpecificDatumReader<Client>(Client.class);
            DataFileReader<Client> dataFileReader = new DataFileReader<Client>(file, clientDatumReader);
            Client client = null;
            while (dataFileReader.hasNext()) {
                client = dataFileReader.next(client);
                logger.info(client.toString());
                logger.info("ID: " + client.getId() + " Name: " + client.getName());
                saveClientToBigQuery(client.getId(), client.getName().toString(), client.getPhone().toString(), client.getAddress().toString());
            }
            dataFileReader.close();
            logger.info("File "+ fileName +" was deserialized");
            boolean success = file.delete();
            if (success) {logger.info("File was deleted");}
        } catch (IOException e) {
            logger.error("Deserialization error:" + e.getMessage());
        }
    }

    private void saveClientToBigQuery(long id, String name, String phone, String address) {
        try {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json")).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        BigQuery bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();

            Map<String, Object> clientFull = new HashMap<>();
            clientFull.put("id", id);
            clientFull.put("name", name);
            clientFull.put("phone", phone);
            clientFull.put("address", address);
            Map<String, Object> clientReq = new HashMap<>();
            clientReq.put("id", id);
            clientReq.put("name", name);

            TableId tableIdFull = TableId.of("test_dataset","client_full");
            TableId tableIdReq = TableId.of("test_dataset","client_required");

            InsertAllResponse responseFull = bigquery.insertAll(InsertAllRequest.newBuilder(tableIdFull)
                    .addRow(clientFull)
                    .build());

            InsertAllResponse responseReq = bigquery.insertAll(InsertAllRequest.newBuilder(tableIdReq)
                    .addRow(clientReq)
                    .build());

            if (responseFull.hasErrors()) {
                for (Map.Entry<Long, List<BigQueryError>> entry : responseFull.getInsertErrors().entrySet()) {
                    logger.error("Query error log entry: " + entry);
                }
            }

            if (responseReq.hasErrors()) {
                for (Map.Entry<Long, List<BigQueryError>> entry : responseReq.getInsertErrors().entrySet()) {
                    logger.error("Query error log entry: " + entry);
                }
            }
        } catch (IOException e) {System.out.println("Error while working with BigQuery");}
    }

    private void authorize() {
        try {
            FileWriter myWriter = new FileWriter("credentials.json");
            myWriter.write("{\n" +
                    "  \"type\": \"" + env.getProperty("crType") + "\",\n" +
                    "  \"project_id\": \"" + env.getProperty("crProjectId") + "\",\n" +
                    "  \"private_key_id\": \"" + env.getProperty("crPrivateKeyId") + "\",\n" +
                    "  \"private_key\": \"" + env.getProperty("crPrivateKey") + "\",\n" +
                    "  \"client_email\": \"" + env.getProperty("crClientEmail") + "\",\n" +
                    "  \"client_id\": \"" + env.getProperty("crClientId") + "\",\n" +
                    "  \"auth_uri\": \"" + env.getProperty("crAuthUri") + "\",\n" +
                    "  \"token_uri\": \"" + env.getProperty("crTokenUri") + "\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"" + env.getProperty("crAuthProvider") + "\",\n" +
                    "  \"client_x509_cert_url\": \"" + env.getProperty("crClientCertUrl") + "\"\n" +
                    "}\n");
            myWriter.close();
            logger.info("Credentials were created!");
        } catch (IOException e) {System.out.println("Error creating credentials");}
    }

}
