package se.helsingborg.oppna.solarie.webapp.version_0_0_1;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.json.JSONArray;
import org.json.JSONException;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.JSONQueryUnmarshaller;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetDefinition;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarienummer;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarium;
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
    writer.println("Primärt sök-API.");
  }

  @Override
  public void writeDocumentationRequest(PrintWriter writer) throws IOException {
    writer.println("{");
    writer.println("  \"reference\": Any value");
    writer.println("  \"explain\": Boolean (default false), true if explaining hits");
    writer.println("  \"score\": Boolean (default true), if items are scored on relevance from query");
    writer.println("  \"sortOrder\": String, e.g. 'score'");
    writer.println("  \"offset\": 0-based item start offset");
    writer.println("  \"length\": Maximum number of returned results");
    writer.println("  \"query\": Query");
//    writer.println("  \"facets\": []");
    writer.println("}");
  }

  @Override
  public void writeDocumentationResponse(PrintWriter writer) throws IOException {
    writer.println("{");
    writer.println("  \"reference\": Same as request");
    writer.println("  \"length\": Total number of matching items, i.e. might be greater than items.length");
    writer.println("  \"timers\": { \"timer name\": milliseconds }");
    writer.println("  \"items\": [{");
    writer.println("    \"index\": 0-based item offset");
    writer.println("    \"score\": If request.score is true");
    writer.println("    \"explanation\": If request.explain is true. HTML explaination of scoring");
    writer.println("    \"type\": Packageless Java class name of instance");
    writer.println("    \"instance\": Actual search result");
    writer.println("  }]");
    writer.println("}");
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

    sortOrders.put("timestamp", new Comparator<SearchResult>() {

      private IdentifiableVisitor<Long> getTimestamp = new IndexableVisitor<Long>() {
        @Override
        public Long visit(Arende ärende) {
          return ärende.getRegistrerad();
        }

        @Override
        public Long visit(Atgard åtgärd) {
          return åtgärd.getRegistrerad();
        }

        @Override
        public Long visit(Dokument dokument) {
          return 0l; // todo
//          return dokument.getRegistrerad();
        }
      };

      @Override
      public int compare(SearchResult o1, SearchResult o2) {
        return o1.getInstance().accept(getTimestamp).compareTo(o2.getInstance().accept(getTimestamp));
      }
    });

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

  private IndexableVisitor<Long> getTimestamp = new IndexableVisitor<Long>(){
    @Override
    public Long visit(Arende ärende) {
      return ärende.getRegistrerad();
    }

    @Override
    public Long visit(Atgard åtgärd) {
      return åtgärd.getRegistrerad();
    }

    @Override
    public Long visit(Dokument dokument) {
      return null; //
    }
  };

  private class GetInstanceJSON implements IdentifiableVisitor<JSONObject> {

    @Override
    public JSONObject visit(Diarium diarium) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));
        json.put("identity", diarium.getIdentity());
        json.put("namn", diarium.getNamn());
        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }

    }

    @Override
    public JSONObject visit(Enhet enhet) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));
        json.put("diarium", enhet.getDiarium().getIdentity());
        json.put("identity", enhet.getIdentity());
        json.put("namn", enhet.getNamn());
        json.put("kod", enhet.getKod());
        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }

    @Override
    public JSONObject visit(Anvandare användare) {
      try {
        JSONObject json = new JSONObject(new LinkedHashMap(20));
        json.put("diarium", användare.getDiarium().getIdentity());
        json.put("identity", användare.getIdentity());
        json.put("namn", användare.getNamn());
        json.put("signatur", användare.getSignatur());
        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }

    private JSONObject factory(Indexable indexable) throws JSONException {
      JSONObject json = new JSONObject(new LinkedHashMap(20));
      json.put("identity", indexable.getIdentity());

      json.put("diarium", indexable.accept(GetDiarium.getInstance()).getIdentity());
      json.put("diarienummer", indexable.accept(GetDiarienummer.getInstance()).toString());

      return json;
    }


    @Override
    public JSONObject visit(Arende ärende) {
      try {
        JSONObject json = factory(ärende);

        json.put("mening", ärende.getMening());

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }


    @Override
    public JSONObject visit(Atgard åtgärd) {
      try {
        JSONObject json = factory(åtgärd);

        json.put("ärende", åtgärd.getÄrende().getIdentity());

        json.put("text", åtgärd.getText());

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }

    @Override
    public JSONObject visit(Dokument dokument) {
      try {
        JSONObject json = factory(dokument);

        if (dokument.getÅtgärd() != null) {
          json.put("åtgärd", dokument.getÅtgärd().getIdentity());

        }

        return json;
      } catch (JSONException je) {
        throw new RuntimeException(je);
      }
    }
  }

  private class Searcher {

    private boolean score;
    private boolean explain;

    private int offset;
    private int length;

    private int end;

    private Set<Identitfiable> instances = new HashSet<>(length * 3);
    IndexableVisitor<Void> gatherInstances = new IndexableVisitor<Void>() {
      @Override
      public Void visit(Arende ärende) {
        instances.add(ärende);
        instances.add(ärende.getDiarium());
        return null;
      }

      @Override
      public Void visit(Atgard åtgärd) {
        instances.add(åtgärd);
        visit(åtgärd.getÄrende());
        return null;
      }

      @Override
      public Void visit(Dokument dokument) {
        instances.add(dokument);
        instances.add(dokument.getDiarium());
        if (dokument.getÅtgärd() != null) {
          visit(dokument.getÅtgärd());
        }

        return null;
      }
    };


    public void doProcess(HttpServletRequest request, HttpServletResponse response, JSONObject requestJSON, JSONObject responseJSON) throws Exception {

      org.json.JSONObject timersJSON = responseJSON.getJSONObject("timers");

      // parse request
      long timerStarted = System.currentTimeMillis();
      if (requestJSON.has("reference")) {
        responseJSON.put("reference", requestJSON.get("reference"));
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
      timerStarted = System.currentTimeMillis();
      Comparator<SearchResult> sortOrder = null;
      if (requestJSON.has("sortOrder")) {
        sortOrder = sortOrders.get(requestJSON.getString("sortOrder"));
        if (sortOrder == null) {
          throw new RuntimeException("Unsupported sort order: " + requestJSON.getString("sortOrder"));
        } else {
          Collections.sort(searchResults, sortOrder);
        }
      }
      timersJSON.put("sort", System.currentTimeMillis() - timerStarted);


      GetInstanceJSON getInstanceJSON = new SearchServlet.GetInstanceJSON();


      // select search results to display
      timerStarted = System.currentTimeMillis();

      JSONArray itemsJSON = new JSONArray();
      responseJSON.put("items", itemsJSON);

      for (int index = offset; index < end && index < searchResults.size(); index++) {
        SearchResult searchResult = searchResults.get(index);
        itemsJSON.put(toJSON(index, searchResult));
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


      for (int groupIndex = offset; groupIndex < end && groupIndex < orderdGroups.size(); groupIndex++) {
        Group group = orderdGroups.get(groupIndex);
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
      for (Identitfiable instance : instances) {
        instancesJSON.put(instance.accept(getInstanceJSON));
      }
      timersJSON.put("instances", System.currentTimeMillis() - timerStarted);


    }

    private JSONObject toJSON(int index, SearchResult searchResult) throws JSONException {

      JSONObject searchResultJSON = new JSONObject(new LinkedHashMap<>(10));

      searchResultJSON.put("index", index);
      if (score) {
        searchResultJSON.put("score", searchResult.getScore());
      }
      if (explain) {
        // todo as JSON
        searchResultJSON.put("explanation", searchResult.getExplanation().toHtml());
      }

      searchResultJSON.put("timestamp", searchResult.getInstance().accept(getTimestamp));

      searchResultJSON.put("type", searchResult.getInstance().getClass().getSimpleName());
      searchResultJSON.put("instance", searchResult.getInstance().getIdentity());

      searchResult.getInstance().accept(gatherInstances);
      return searchResultJSON;
    }

  }

}
