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
public class AnvandareFacet extends Facet {

  public AnvandareFacet() {
    super("Användare");
  }

  private class GatherAnvandare implements IndexableVisitor<Void> {
    private Set<Anvandare> användarna = new HashSet<>();

    @Override
    public Void visit(Arende ärende) {
      if (ärende.getHandläggare() != null) {
        användarna.add(ärende.getHandläggare());
      }
      if (ärende.getRegistrator() != null) {
        användarna.add(ärende.getRegistrator());
      }
      if (ärende.getSenasteModifierare() != null) {
        användarna.add(ärende.getSenasteModifierare());
      }
      if (ärende.getÄgare() != null) {
        användarna.add(ärende.getÄgare());
      }
      return null;
    }

    @Override
    public Void visit(Atgard åtgärd) {
      if (åtgärd.getÄgare() != null) {
        användarna.add(åtgärd.getÄgare());
      }
      return null;
    }

    @Override
    public Void visit(Dokument dokument) {
      return null;
    }

    private Set<Anvandare> getAnvändarna() {
      return användarna;
    }
  }

  private class MatchesVisitor implements IndexableVisitor<Boolean> {

    private Anvandare användare;

    private MatchesVisitor(Anvandare användare) {
      this.användare = användare;
    }

    @Override
    public Boolean visit(Arende ärende) {
      return användare.equals(ärende.getHandläggare())
          || användare.equals(ärende.getRegistrator())
          || användare.equals(ärende.getSenasteModifierare())
          || användare.equals(ärende.getÄgare());
    }

    @Override
    public Boolean visit(Atgard åtgärd) {
      return användare.equals(åtgärd.getÄgare());
    }

    @Override
    public Boolean visit(Dokument dokument) {
      return false; // todo
    }
  }


  @Override
  protected List<FacetValue> valuesFactory(List<SearchResult> searchResults) {
    GatherAnvandare gatherAnvandare = new GatherAnvandare();
    for (SearchResult searchResult : searchResults) {
      searchResult.getIndexable().accept(gatherAnvandare);
    }



    List<FacetValue> values = new ArrayList<>(gatherAnvandare.getAnvändarna().size());
    for (final Anvandare användare : gatherAnvandare.getAnvändarna()) {

      final MatchesVisitor matcher = new MatchesVisitor(användare);

      values.add(new FacetValue(searchResults, användare.getNamn() == null ? användare.getSignatur() : användare.getNamn()) {

        @Override
        public Facet getFacet() {
          return AnvandareFacet.this;
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
      return new AnvandareFacet();
    }

  }
}
