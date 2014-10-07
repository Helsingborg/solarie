package se.helsingborg.oppna.solarie.index.facet;

import org.json.JSONException;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-10-03 17:41
 */
public abstract class FacetValue {

  private String name;
  private Set<SearchResult> matches;
  private Collection<SearchResult> searchResults;

  protected FacetValue(Collection<SearchResult> searchResults, String name) {
    this.name = name;
    this.searchResults = searchResults;
    matches = new HashSet<>(Math.min(searchResults.size(), 256));
    for (SearchResult searchResult : searchResults) {
      if (matches(searchResult)) {
        matches.add(searchResult);
      }
    }
  }

  public abstract boolean matches(SearchResult searchResult);

  public JSONObject toJSON() throws JSONException {
    JSONObject facetValueJSON = new JSONObject();
    facetValueJSON.put("name", getName());
    facetValueJSON.put("matches", getMatches().size());
    facetValueJSON.put("query", (JSONObject)null);
    return facetValueJSON;
  }

  public void removeMatchingSearchResults() {
    searchResults.removeAll(matches);
  }


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
