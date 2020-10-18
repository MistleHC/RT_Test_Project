package cloud.test.web.Controllers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cloud.test.web.DAO.AvroDao;
import example.gcp.Client;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class HelloWorldController {

  private static String project;
  private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
  private final AvroDao avroDao;

  @Autowired
  public HelloWorldController (AvroDao avroDao) {
    this.avroDao = avroDao;
  }

  @GetMapping("/")
  public String helloWorld(Model model) {
      logger.info("Hello from Cloud Run Controller!");
    // If the custom environment variable GOOGLE_CLOUD_PROJECT is not set
    // check the Cloud Run metadata server for the Project Id.
    project = System.getenv("GOOGLE_CLOUD_PROJECT");
    if (project == null) {
      project = getProjectId();
    }

    // Get Cloud Run environment variables.
    String revision = System.getenv("K_REVISION") == null ? "???" : System.getenv("K_REVISION");
    String service = System.getenv("K_SERVICE") == null ? "???" : System.getenv("K_SERVICE");

    // Set variables in html template.
    model.addAttribute("revision", revision);
    model.addAttribute("service", service);
    model.addAttribute("project", project);

    avroDao.checkNewFiles();
    logger.info(Thread.currentThread().getName()+" file check was executed.");
    return "index";
  }

  /**
   * Get the Project Id from GCP metadata server
   *
   * @return GCP Project Id or null
   */
  public static String getProjectId() {
    OkHttpClient ok =
        new OkHttpClient.Builder()
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .build();

    String metadataUrl = "http://metadata.google.internal/computeMetadata/v1/project/project-id";
    Request request =
        new Request.Builder().url(metadataUrl).addHeader("Metadata-Flavor", "Google").get().build();

    String project = null;
    try {
      Response response = ok.newCall(request).execute();
      project = response.body().string();
    } catch (IOException e) {
      logger.error("Error retrieving Project Id");
    }
    return project;
  }
}
