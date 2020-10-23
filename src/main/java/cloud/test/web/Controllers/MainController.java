package cloud.test.web.Controllers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cloud.test.web.DAO.AvroDao;
import cloud.test.web.EventHandlers.AvroBucketEventHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public final class MainController {

  private static String project;
  private static final Logger logger = LoggerFactory.getLogger(MainController.class);
  private final AvroDao avroDao;

  @Autowired
  public MainController(AvroDao avroDao) {
    this.avroDao = avroDao;
  }

  @GetMapping("/")
  public String helloWorld(Model model) {
    logger.info("Cloud Run Controller request");

    project = System.getenv("GOOGLE_CLOUD_PROJECT");
    if (project == null) {
      project = getProjectId();
    }

    String revision = System.getenv("K_REVISION") == null ? "???" : System.getenv("K_REVISION");
    String service = System.getenv("K_SERVICE") == null ? "???" : System.getenv("K_SERVICE");

    model.addAttribute("revision", revision);
    model.addAttribute("service", service);
    model.addAttribute("project", project);

    logger.info(Thread.currentThread().getName()+" file check was executed.");
    return "index";
  }

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
