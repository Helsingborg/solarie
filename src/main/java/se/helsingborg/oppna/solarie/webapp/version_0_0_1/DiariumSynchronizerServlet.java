package se.helsingborg.oppna.solarie.webapp.version_0_0_1;

import org.json.JSONException;
import se.helsingborg.oppna.solarie.DiariumSynchronizer;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.util.JSONObject;
import se.helsingborg.oppna.solarie.webapp.JSONPostService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * @author kalle
 * @since 2014-10-03 04:49
 */
public class DiariumSynchronizerServlet extends JSONPostService {

  @Override
  public void writeDocumentationDescription(PrintWriter writer) throws IOException {
    writer.println("Anrop till denna startar en synkronisering mot ett givet diarium.");
    writer.println("Svaret ankommer efter det att synkroniseringen är avslutad.");
  }

  @Override
  public void writeDocumentationRequest(PrintWriter writer) throws IOException {
    writer.println("{");
    writer.println("  \"identity\": long");
    writer.println("}");
  }

  @Override
  public void writeDocumentationResponse(PrintWriter writer) throws IOException {
    writer.println("{ ");
    writer.println("  \"success\": false if failed");
    writer.println("  \"timers\": { ");
    writer.println("    \"read\": milliseconds");
    writer.println("    \"index\": milliseconds");
    writer.println("  } ");
    writer.println("}");
  }

  @Override
  public void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception {

    org.json.JSONObject timersJSON = responseJSON.getJSONObject("timers");

    long timerStarted = System.currentTimeMillis();

    Long identity = requestJSON.getLong("identity");

    Diarium diarium = Solarie.getInstance().getPrevayler().prevalentSystem().getDiariumByIdentity().get(identity);
    if (diarium == null) {
      throw new NoSuchElementException("No diarium with identity " + identity);
    }

    DiariumSynchronizer.getInstance(diarium).synchronize();

    timersJSON.put("synchronize", System.currentTimeMillis() - timerStarted);
    timerStarted = System.currentTimeMillis();


  }
}
