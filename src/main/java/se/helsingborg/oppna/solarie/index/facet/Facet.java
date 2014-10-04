package se.helsingborg.oppna.solarie.index.facet;

import org.json.JSONArray;
import org.json.JSONException;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.*;

/**
 * @author kalle
 * @since 2014-10-03 10:13
 */
public abstract class Facet {

  private String name;

  protected Facet(String name) {
    this.name = name;
  }

  private List<FacetValue> values;

  private Set<SearchResult> matches;




  /**
   * True if to be added.
   * @param searchResults
   * @return
   */
  public void populate(List<SearchResult> searchResults) {
    values = valuesFactory(searchResults);
    matches = new HashSet<>(Math.min(256, searchResults.size()));
    for (FacetValue value : values) {
      matches.addAll(value.getMatches());
    }
    for (Iterator<FacetValue> iterator = values.iterator(); iterator.hasNext(); ) {
      FacetValue facetValue = iterator.next();
      if (facetValue.getMatches().size() == searchResults.size()) {
        iterator.remove();
      }
    }

    Collections.sort(values, new Comparator<FacetValue>() {
      @Override
      public int compare(FacetValue o1, FacetValue o2) {
        return  o2.getMatches().size() - o1.getMatches().size();
      }
    });
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject facetJSON = new JSONObject();
    facetJSON.put("name", getName());
    facetJSON.put("matches", getMatches().size());

    JSONArray valuesJSON = new JSONArray();
    facetJSON.put("values", valuesJSON);
    for (FacetValue value : getValues()) {
      valuesJSON.put(value.toJSON());
    }

    return facetJSON;
  }

  public List<FacetValue> getValues() {
    return values;
  }

  protected abstract List<FacetValue> valuesFactory(List<SearchResult> searchResults);

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<SearchResult> getMatches() {
    return matches;
  }
}
