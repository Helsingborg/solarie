package se.helsingborg.oppna.solarie.index.facet.impl;

import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetFactory;
import se.helsingborg.oppna.solarie.index.facet.FacetValue;

import java.util.*;

/**
 * @author kalle
 * @since 2014-10-03 19:40
 */
public class DiariumFacet extends Facet {

  public DiariumFacet() {
    super("Diarier");
  }

  @Override
  public List<FacetValue> valuesFactory(List<SearchResult> searchResults) {
    Set<Diarium> diarier = new HashSet<>();
    for (SearchResult searchResult : searchResults) {
      diarier.add(searchResult.getIndexable().getDiarium());
    }
    List<FacetValue> facetValues = new ArrayList<>(diarier.size());
    for (final Diarium diarium : diarier) {
      facetValues.add(new FacetValue(searchResults, diarium.getNamn()) {

        @Override
        public Facet getFacet() {
          return DiariumFacet.this;
        }

        @Override
        public boolean matches(SearchResult searchResult) {
          return searchResult.getIndexable().getDiarium().equals(diarium);
        }

      });
    }
    return facetValues;


  }

  public static class Factory implements FacetFactory {

    @Override
    public Facet factory() {
      return new DiariumFacet();
    }

  }

}
