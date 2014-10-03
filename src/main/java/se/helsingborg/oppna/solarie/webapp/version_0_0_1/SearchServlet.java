package se.helsingborg.oppna.solarie.webapp.version_0_0_1;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.json.JSONArray;
import org.json.JSONException;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Dokument;
import se.helsingborg.oppna.solarie.domain.IndexableVisitor;
import se.helsingborg.oppna.solarie.index.JSONQueryUnmarshaller;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.util.JSONObject;
import se.helsingborg.oppna.solarie.webapp.JSONPostService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author kalle
 * @since 2014-09-16 22:48
 */
public class SearchServlet extends JSONPostService {

  @Override
  public void writeDocumentationDescription(PrintWriter writer) throws IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void writeDocumentationRequest(PrintWriter writer) throws IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void writeDocumentationResponse(PrintWriter writer) throws IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  private Map<String, Comparator<SearchResult>> sortOrders = new HashMap<>();

  @Override
  public void init() throws ServletException {
    sortOrders.put("score", new Comparator<SearchResult>() {
      @Override
      public int compare(SearchResult o1, SearchResult o2) {
        if (o1.getScore() != null && o2.getScore() != null) {
          return o2.getScore().compareTo(o1.getScore());
        } else if (o1.getScore() == null && o2.getScore() == null) {
          return 0;
        } else if (o1.getScore() != null) {
          return -1;
        } else {
          return 1;
        }
      }
    });

    sortOrders.put(null, sortOrders.get("score"));
  }

  @Override
  public void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception {

    org.json.JSONObject timersJSON = responseJSON.getJSONObject("timers");

    long timerStarted = System.currentTimeMillis();
    if (requestJSON.has("reference")) {
      responseJSON.put("reference", requestJSON.get("reference"));
    }
    Query query = !requestJSON.has("query") ? new MatchAllDocsQuery() : new JSONQueryUnmarshaller().parseJsonQuery(requestJSON.getJSONObject("query"));
    boolean score = requestJSON.getBoolean("score", true);
    boolean explain = requestJSON.getBoolean("explain", false);
    timersJSON.put("parse", System.currentTimeMillis() - timerStarted);

    timerStarted = System.currentTimeMillis();
    List<SearchResult> searchResults = Solarie.getInstance().getIndex().search(query, score, explain);
    responseJSON.put("length", searchResults.size());
    timersJSON.put("collect", System.currentTimeMillis() - timerStarted);

    timerStarted = System.currentTimeMillis();
    if (requestJSON.has("sortOrder")) {
      Comparator<SearchResult> sortOrder = sortOrders.get(requestJSON.getString("sortOrder"));
      if (sortOrder == null) {
        throw new RuntimeException("Unsupported sort order: " + requestJSON.getString("sortOrder"));
      } else {
        Collections.sort(searchResults, sortOrder);
      }
    }
    timersJSON.put("sort", System.currentTimeMillis() - timerStarted);

    timerStarted = System.currentTimeMillis();
    // todo facets
    timersJSON.put("facet", System.currentTimeMillis() - timerStarted);


    timerStarted = System.currentTimeMillis();


    int offset = requestJSON.getInt("offset", 0);
    int length = requestJSON.getInt("length", 100);

    int end = offset + length;
    int total = searchResults.size();

    JSONArray itemsJSON = new JSONArray();
    responseJSON.put("items", itemsJSON);

    IndexableJSONVisitor visitor = new IndexableJSONVisitor();

    for (int index = offset; index < end && index < total; index++) {
      SearchResult searchResult = searchResults.get(index);
      JSONObject searchResultJSON = new JSONObject(new LinkedHashMap<>(10));
      itemsJSON.put(searchResultJSON);

      searchResultJSON.put("index", index);
      if (score) {
        searchResultJSON.put("score", searchResult.getScore());
      }
      if (explain) {
        // todo as JSON
        searchResultJSON.put("explanation", searchResult.getExplanation().toHtml());
      }
      searchResultJSON.put("type", searchResult.getIndexable().getClass().getSimpleName());
      searchResultJSON.put("instance", searchResult.getIndexable().accept(visitor));
    }
    timersJSON.put("assemble", System.currentTimeMillis() - timerStarted);

  }

  private class IndexableJSONVisitor implements IndexableVisitor<JSONObject> {


    @Override
    public JSONObject visit(Arende ärende) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));

        json.put("identity", ärende.getIdentity());
        json.put("diarium", ärende.getDiarium().getIdentity());
        json.put("diarienummer", ärende.getDiarienummer().toString());

        json.put("mening", ärende.getMening());

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }

    @Override
    public JSONObject visit(Atgard åtgärd) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));

        json.put("identity", åtgärd.getIdentity());
        json.put("diarium", åtgärd.getDiarium().getIdentity());
        json.put("ärende", åtgärd.getÄrende().getIdentity());
        json.put("diarienummer", åtgärd.getÄrende().getDiarienummer().toString());

        json.put("text", åtgärd.getText());

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }

    @Override
    public JSONObject visit(Dokument dokument) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));

        json.put("identity", dokument.getIdentity());
        json.put("diarium", dokument.getDiarium().getIdentity());

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }
  }
}