package se.helsingborg.oppna.solarie.index.facet;

import se.helsingborg.oppna.solarie.index.SearchResult;

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

  public void populate(List<SearchResult> searchResults) {
    matches = new HashSet<>(Math.min(256, searchResults.size()));
    values = valuesFactory(searchResults);
    for (FacetValue value : values) {
      matches.addAll(value.getMatches());
    }
    Collections.sort(values, new Comparator<FacetValue>() {
      @Override
      public int compare(FacetValue o1, FacetValue o2) {
        return  o2.getMatches().size() - o1.getMatches().size();
      }
    });
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
