package se.helsingborg.oppna.solarie.webapp;

import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.helsingborg.oppna.solarie.util.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

/**
 * @author kalle
 * @since 2014-10-03 05:32
 */
public abstract class JSONPostService extends HttpServlet {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  public abstract void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception;


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (log.isDebugEnabled()) {
      log.debug("Documentation requested");
    }

    response.setCharacterEncoding("UTF8");
    response.setContentType("text/plain");

    response.getWriter().println("Detta är dokumentationen för den HTTP servicen som svarar med JSON i UTF8 på POST förfrågningar till denna URL.");
    response.getWriter().println("");
    writeDocumentationDescription(response.getWriter());
    response.getWriter().println("");
    response.getWriter().println("= Förfrågan =");
    response.getWriter().println("");
    writeDocumentationRequest(response.getWriter());
    response.getWriter().println("");
    response.getWriter().println("= Svar =");
    response.getWriter().println("");
    writeDocumentationResponse(response.getWriter());


  }

  public abstract void writeDocumentationResponse(PrintWriter writer) throws IOException;

  public abstract void writeDocumentationRequest(PrintWriter writer) throws IOException;

  public abstract void writeDocumentationDescription(PrintWriter writer) throws IOException;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (log.isDebugEnabled()) {
      log.debug("Incoming request");
    }

    try {

      long totalTimerStarted = System.currentTimeMillis();

      // todo consider writing JSON to the writer to save heap

      JSONObject requestJSON = null;
      JSONObject responseJSON = new JSONObject(new LinkedHashMap<>(10));
      JSONObject timersJSON = new JSONObject(new LinkedHashMap<>(10));
      responseJSON.put("timers", timersJSON);

      try {

        long timerStarted = System.currentTimeMillis();
        requestJSON = new JSONObject(new JSONTokener(new InputStreamReader(request.getInputStream(), request.getCharacterEncoding())));
        if (log.isInfoEnabled()) {
          log.info("Incoming request: " + requestJSON.toString());
        }
        timersJSON.put("request", System.currentTimeMillis() - timerStarted);

        doProcess(request, response, requestJSON, responseJSON);

      } catch (Exception e) {
        log.error(requestJSON != null ? requestJSON.toString() : "Caught exception at a point where requestJSON is null", e);
        responseJSON.put("success", false);

      } finally {
        timersJSON.put("total", System.currentTimeMillis() - totalTimerStarted);

      }


      response.setCharacterEncoding("UTF8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF8"));

      if (log.isDebugEnabled()) {
        log.debug("Reponse sent to reference " + requestJSON.get("reference", null) + ": " + responseJSON.toString());
      }


    } catch (Exception e) {
      log.error("Caught unhandled exception", e);
    }

  }


}
