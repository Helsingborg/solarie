package se.helsingborg.oppna.solarie.webapp.version_0_0_1;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.JSONQueryUnmarshaller;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetDefinition;
import se.helsingborg.oppna.solarie.util.JSONObject;
import se.helsingborg.oppna.solarie.webapp.JSONPostService;
import se.helsingborg.oppna.solarie.webapp.version_0_0_1.sort.Score;
import se.helsingborg.oppna.solarie.webapp.version_0_0_1.sort.Timestamp;
import se.helsingborg.oppna.solarie.webapp.version_0_0_1.visitors.GetSearchResultInstanceJSON;
import se.helsingborg.oppna.solarie.webapp.version_0_0_1.visitors.GetTimestamp;

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
    writer.println("Primärt sök-API.");
  }

  @Override
  public void writeDocumentationRequest(PrintWriter writer) throws IOException {
    writer.println("{");
    writer.println("  \"cached\": String, template name for this query");
    writer.println("  \"reference\": Any value");
    writer.println("  \"explain\": Boolean (default false), true if explaining hits");
    writer.println("  \"score\": Boolean (default true), if items are scored on relevance from query");
    writer.println("  \"sortOrder\": String, 'score' or 'timestamp'");
    writer.println("  \"offset\": 0-based item start offset");
    writer.println("  \"length\": Maximum number of returned results");
    writer.println("  \"query\": Query");
    writer.println("}");
  }

  @Override
  public void writeDocumentationResponse(PrintWriter writer) throws IOException {
    writer.println("{");
    writer.println("  \"reference\": Same as request");
    writer.println("  \"length\": Total number of matching items, i.e. might be greater than items.length");
    writer.println("  \"timers\": { \"timer name\": milliseconds }");
    writer.println("  \"groups\": [{");
    writer.println("    \"index\": 0-based item offset");
    writer.println("    \"score\": If request.score is true");
    writer.println("    \"instance\": (Long) integer value, identity of instance");
    writer.println("  }]");
    writer.println("  \"instances\": [{\n");
    writer.println("    \"identity\": (Long) integer value\n");
    writer.println("    \"enhet\": (Long) integer value\n");
    writer.println("    ...\n");
    writer.println("  }]\n");
    writer.println("}");
  }

  private Map<String, Comparator<SearchResult>> sortOrders = new HashMap<>();

  private Map<String, JSONObject> cachedResults = new HashMap<>();

  @Override
  public void init() throws ServletException {
    sortOrders.put("score", new Score());
    sortOrders.put("timestamp", new Timestamp());

    // default
    sortOrders.put(null, sortOrders.get("score"));
  }

  @Override
  public void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception {
    new Searcher().doProcess(request, response, requestJSON, responseJSON);
  }

  private class Group {

    private Group(Indexable root) {
      this.root = root;
    }

    private Float bestScore;
    private Indexable root;
    private List<SearchResult> searchResults;

    private void addSearchResult(SearchResult searchResult) {
      if (searchResults == null) {
        searchResults = new ArrayList<>(25);
      }
      searchResults.add(searchResult);
      if (searchResult.getScore() != null) {
        if (bestScore == null) {
          bestScore = searchResult.getScore();
        } else if (bestScore < searchResult.getScore()) {
          bestScore = searchResult.getScore();
        }
      }
    }
  }



  private class Searcher {

    private boolean score;
    private boolean explain;

    private int offset;
    private int length;

    private int end;

    private Set<Identitfiable> instances = new HashSet<Identitfiable>(length * 3) {
      @Override
      public boolean add(Identitfiable identitfiable) {
        return identitfiable != null && super.add(identitfiable);
      }
    };
    IndexableVisitor<Void> gatherInstances = new IndexableVisitor<Void>() {
      @Override
      public Void visit(Arende ärende) {
        instances.add(ärende);
        instances.add(ärende.getDiarium());
        instances.add(ärende.getEnhet());
        return null;
      }

      @Override
      public Void visit(Atgard åtgärd) {
        visit(åtgärd.getÄrende());
        instances.add(åtgärd);
        instances.add(åtgärd.getEnhet());
        return null;
      }

      @Override
      public Void visit(Dokument dokument) {
        if (dokument.getÅtgärd() != null) {
          visit(dokument.getÅtgärd());
        }
        instances.add(dokument);
        instances.add(dokument.getDiarium());
//        return null;
        throw new UnsupportedOperationException();
      }
    };


    public void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception {

      org.json.JSONObject timersJSON = responseJSON.getJSONObject("timers");

      // parse request
      long timerStarted = System.currentTimeMillis();
      if (requestJSON.has("reference")) {
        responseJSON.put("reference", requestJSON.get("reference"));
      }

      String cached = null;
      if (requestJSON.has("cached")) {
        cached = requestJSON.getString("cached");
        JSONObject cachedJSON = cachedResults.get(cached);
        if (cachedJSON != null) {
          for (Iterator keys = cachedJSON.keys(); keys.hasNext(); ) {
            String key = (String) keys.next();
            responseJSON.put(key, cachedJSON.get(key));
          }
          return;
        }
      }

      Query query = !requestJSON.has("query") ? new MatchAllDocsQuery() : new JSONQueryUnmarshaller().parseJsonQuery(requestJSON.getJSONObject("query"));
      score = requestJSON.getBoolean("score", true);
      explain = requestJSON.getBoolean("explain", false);

      offset = requestJSON.getInt("offset", 0);
      length = requestJSON.getInt("length", 100);

      end = offset + length;
      timersJSON.put("parse", System.currentTimeMillis() - timerStarted);

      // collect search results
      timerStarted = System.currentTimeMillis();
      final List<SearchResult> searchResults = Solarie.getInstance().getIndex().search(query, score, explain);
      responseJSON.put("length", searchResults.size());
      timersJSON.put("collect", System.currentTimeMillis() - timerStarted);

      // create facets
      timerStarted = System.currentTimeMillis();

      final JSONArray facetsJSON = new JSONArray();
      responseJSON.put("facets", facetsJSON);

      List<FacetDefinition> facets = new ArrayList<>(Solarie.getInstance().getIndex().getFacets());
      Thread[] facetThreads = new Thread[facets.size()];
      for (int i = 0; i < facets.size(); i++) {
        final Facet facet = facets.get(i).facetFactory();
        facetThreads[i] = new Thread(new Runnable() {
          @Override
          public void run() {
            facet.populate(searchResults);
            if (!facet.getValues().isEmpty()) {
              try {
                facetsJSON.put(facet.toJSON());
              } catch (Exception e) {
                log.error("Exception while creating JSON for facet " + facet.getName(), e);
              }
            }
          }
        });
        facetThreads[i].setDaemon(true);
        facetThreads[i].start();
      }

      for (Thread thread : facetThreads) {
        thread.join();
      }

      timersJSON.put("create_facets", System.currentTimeMillis() - timerStarted);


      // sort order
      Comparator<SearchResult> sortOrder = null;
      if (!searchResults.isEmpty()) {
        timerStarted = System.currentTimeMillis();
        if (requestJSON.has("sortOrder")) {
          sortOrder = sortOrders.get(requestJSON.getString("sortOrder"));
          if (sortOrder == null) {
            throw new RuntimeException("Unsupported sort order: " + requestJSON.getString("sortOrder"));
          } else {
            Collections.sort(searchResults, sortOrder);
          }


        }


        if (score) {
          float topScore;

          Comparator<SearchResult> scoreOrder = sortOrders.get("score");
          if (sortOrder == scoreOrder) {
            topScore = searchResults.get(0).getScore();
          } else {
            List<SearchResult> scored = new ArrayList<>(searchResults);
            Collections.sort(scored, scoreOrder);
            topScore = scored.get(0).getScore();
          }
          float factor = 1f / topScore;
          for (SearchResult searchResult : searchResults) {
            searchResult.setNormalizedScore(factor * searchResult.getScore());
          }

        }


        timersJSON.put("sort", System.currentTimeMillis() - timerStarted);
      }


      timersJSON.put("assemble", System.currentTimeMillis() - timerStarted);


      // groups results
      timerStarted = System.currentTimeMillis();
      IndexableVisitor<Indexable> getGroupVisitor = new IndexableVisitor<Indexable>() {
        @Override
        public Indexable visit(Arende ärende) {
          return ärende;
        }

        @Override
        public Indexable visit(Atgard åtgärd) {
          return åtgärd.getÄrende();
        }

        @Override
        public Indexable visit(Dokument dokument) {
          if (dokument.getÅtgärd() != null) {
            return visit(dokument.getÅtgärd());
          }
          return dokument;
        }
      };

      Map<Indexable, SearchResult> searchResultsByIndexable = new HashMap<>(searchResults.size());
      for (SearchResult searchResult : searchResults) {
        searchResultsByIndexable.put(searchResult.getInstance(), searchResult);
      }

      Map<Indexable, Group> groups = new HashMap<>(instances.size());
      for (SearchResult searchResult : searchResults) {
        Indexable root = searchResult.getInstance().accept(getGroupVisitor);
        Group group = groups.get(root);
        if (group == null) {
          group = new Group(root);
          groups.put(root, group);
        }
        group.addSearchResult(searchResult);
      }

      JSONArray groupsJSON = new JSONArray(new ArrayList(groups.size()));
      responseJSON.put("groups", groupsJSON);

      if (sortOrder != null) {
        for (Group group : groups.values()) {
          if (group.searchResults != null) {
            Collections.sort(group.searchResults, sortOrder);
          }
        }
      }

      List<Group> orderdGroups = new ArrayList<>(groups.values());
      if (sortOrder != null) {
        final Comparator<SearchResult> finalSortOrder = sortOrder;
        Collections.sort(orderdGroups, new Comparator<Group>() {
          @Override
          public int compare(Group o1, Group o2) {
            return finalSortOrder.compare(o1.searchResults.get(0), o2.searchResults.get(0));
          }
        });
      }

      // move defaults to top, i.e. parent instances of this

      for (int groupIndex = offset; groupIndex < end && groupIndex < orderdGroups.size(); groupIndex++) {
        final Group group = orderdGroups.get(groupIndex);
        Collections.sort(group.searchResults, sortOrder);

        group.searchResults.get(0).getInstance().accept(new IndexableVisitor<Void>() {
          @Override
          public Void visit(Arende ärende) {
            return null;
          }

          @Override
          public Void visit(Atgard åtgärd) {
            SearchResult ärende = null;
            for (int i = 1; i < group.searchResults.size(); i++) {
              SearchResult searchResult = group.searchResults.get(i);
              if (searchResult.getInstance() == åtgärd.getÄrende()) {
                ärende = searchResult;
                break;
              }
            }
            if (ärende != null) {
              group.searchResults.remove(ärende);
            } else {
              ärende = new SearchResult();
              ärende.setScore(0f);
              ärende.setInstance(åtgärd.getÄrende());
            }

            group.searchResults.add(1, ärende);

            return null;
          }

          @Override
          public Void visit(Dokument dokument) {
            if (dokument.getÅtgärd() != null) {
              SearchResult åtgärd = null;
              SearchResult ärende = null;
              for (int i = 1; i < group.searchResults.size(); i++) {
                SearchResult searchResult = group.searchResults.get(i);
                if (searchResult.getInstance() == dokument.getÅtgärd()) {
                  åtgärd = searchResult;
                }
                if (searchResult.getInstance() == dokument.getÅtgärd().getÄrende()) {
                  ärende = searchResult;
                }
              }
              if (åtgärd != null) {
                group.searchResults.remove(åtgärd);
                group.searchResults.remove(ärende);
              } else {
                åtgärd = new SearchResult();
                åtgärd.setScore(0f);
                åtgärd.setInstance(dokument.getÅtgärd());
                ärende = new SearchResult();
                ärende.setScore(0f);
                ärende.setInstance(dokument.getÅtgärd());
              }


              group.searchResults.add(1, åtgärd);
              group.searchResults.add(2, ärende);

            }
            return null;
          }
        });


        JSONObject groupJSON = new JSONObject();
        groupsJSON.put(groupJSON);
        groupJSON.put("root", group.root.getIdentity());
        JSONArray groupItemsJSON = new JSONArray();
        groupJSON.put("items", groupItemsJSON);
        List<SearchResult> groupItems = group.searchResults;
        for (int itemIndex = 0; itemIndex < groupItems.size(); itemIndex++) {
          SearchResult searchResult = groupItems.get(itemIndex);
          groupItemsJSON.put(toJSON(itemIndex, searchResult));
          searchResult.getInstance().accept(gatherInstances);
        }
      }


      timersJSON.put("group", System.currentTimeMillis() - timerStarted);


      // instances
      timerStarted = System.currentTimeMillis();
      JSONArray instancesJSON = new JSONArray(new ArrayList(instances.size()));
      responseJSON.put("instances", instancesJSON);
      instances.remove(null); // we might have added null values..
      for (Identitfiable instance : instances) {
        instancesJSON.put(instance.accept(GetSearchResultInstanceJSON.getInstance()));
      }
      timersJSON.put("instances", System.currentTimeMillis() - timerStarted);

      if (cached != null) {
        JSONObject cachedJSON = new JSONObject(new JSONTokener(responseJSON.toString()));
        cachedJSON.remove("reference");
        timersJSON = cachedJSON.getJSONObject("timers");
        for (Iterator keys = timersJSON.keys(); keys.hasNext(); ) {
          String key = (String) keys.next();
          timersJSON.put(key, 0);
        }
        cachedResults.put(cached, cachedJSON);
      }
    }

    private JSONObject toJSON(int index, SearchResult searchResult) throws JSONException {

      JSONObject searchResultJSON = new JSONObject(new LinkedHashMap<>(10));

      searchResultJSON.put("index", index);
      if (score) {
        searchResultJSON.put("score", searchResult.getScore());
        searchResultJSON.put("normalizedScore", searchResult.getNormalizedScore());
      }
      if (explain) {
        // todo as JSON
        searchResultJSON.put("explanation", searchResult.getExplanation().toHtml());
      }

      searchResultJSON.put("timestamp", searchResult.getInstance().accept(GetTimestamp.getInstance()));

      searchResultJSON.put("type", searchResult.getInstance().getClass().getSimpleName());
      searchResultJSON.put("instance", searchResult.getInstance().getIdentity());

      searchResult.getInstance().accept(gatherInstances);
      return searchResultJSON;
    }

  }

}
