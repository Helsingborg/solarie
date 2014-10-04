package se.helsingborg.oppna.solarie.index.facet.impl;

import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetFactory;
import se.helsingborg.oppna.solarie.index.facet.FacetValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-10-03 20:12
 */
public class EnhetFacet extends Facet {


  public EnhetFacet() {
    super("Enheter");
  }

  private class GatherEnheter implements IndexableVisitor<Void> {
    private Set<Enhet> enheterna = new HashSet<>();

    @Override
    public Void visit(Arende ärende) {
      enheterna.add(ärende.getEnhet());
      return null;
    }

    @Override
    public Void visit(Atgard åtgärd) {
      enheterna.add(åtgärd.getEnhet());
      return null;
    }

    @Override
    public Void visit(Dokument dokument) {
      if(dokument.getÅtgärd() != null) {
        enheterna.add(dokument.getÅtgärd().getEnhet());
      }
      return null;
    }

    private Set<Enhet> getEnheterna() {
      return enheterna;
    }
  }

  private class MatchesVisitor implements IndexableVisitor<Boolean> {

    private Enhet enhet;

    private MatchesVisitor(Enhet enhet) {
      this.enhet = enhet;
    }

    @Override
    public Boolean visit(Arende ärende) {
      return enhet.equals(ärende.getEnhet());
    }

    @Override
    public Boolean visit(Atgard åtgärd) {
      return enhet.equals(åtgärd.getEnhet());
    }

    @Override
    public Boolean visit(Dokument dokument) {
      if (dokument.getÅtgärd() != null) {
        return enhet.equals(dokument.getÅtgärd().getEnhet());
      }
      return false;
    }
  }


  @Override
  protected List<FacetValue> valuesFactory(List<SearchResult> searchResults) {
    GatherEnheter gatherEnheter = new GatherEnheter();
    for (SearchResult searchResult : searchResults) {
      searchResult.getIndexable().accept(gatherEnheter);
    }


    List<FacetValue> values = new ArrayList<>(gatherEnheter.getEnheterna().size());
    for (final Enhet enhet : gatherEnheter.getEnheterna()) {

      final MatchesVisitor matcher = new MatchesVisitor(enhet);

      values.add(new FacetValue(searchResults, enhet.getNamn() == null ? enhet.getKod() : enhet.getNamn()) {

        @Override
        public Facet getFacet() {
          return EnhetFacet.this;
        }

        @Override
        public boolean matches(SearchResult searchResult) {
          return searchResult.getIndexable().accept(matcher);
        }
      });
    }
    return values;
  }

  public class Factory implements FacetFactory {

    @Override
    public Facet factory() {
      return new EnhetFacet();
    }

  }
}
