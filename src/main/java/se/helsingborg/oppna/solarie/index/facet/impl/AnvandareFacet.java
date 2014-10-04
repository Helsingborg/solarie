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

  final Set<Anvandare> anvandarna = new HashSet<>();


  public AnvandareFacet() {
    super("Användare");
  }

  private class GatherAnvandare implements IndexableVisitor<Void> {
    private Set<Anvandare> anvandarna = new HashSet<>();

    @Override
    public Void visit(Arende ärende) {
      anvandarna.add(ärende.getHandläggare());
      anvandarna.add(ärende.getRegistrator());
      anvandarna.add(ärende.getSenasteModifierare());
      anvandarna.add(ärende.getÄgare());
      return null;
    }

    @Override
    public Void visit(Atgard åtgärd) {
//            anvandare.add(åtgärd.getHandläggare());
//            anvandare.add(åtgärd.getRegistrator());
//            anvandare.add(åtgärd.getSenasteModifierare());
      anvandarna.add(åtgärd.getÄgare());
      return null;
    }

    @Override
    public Void visit(Dokument dokument) {
      return null;
    }

    private Set<Anvandare> getAnvandarna() {
      return anvandarna;
    }
  }

  private class MatchesVisitor implements IndexableVisitor<Boolean> {

    private Set<Anvandare> anvandarna = new HashSet<>();

    private MatchesVisitor(GatherAnvandare gatherAnvandare) {
      this(gatherAnvandare.getAnvandarna());
    }

    private MatchesVisitor(Set<Anvandare> anvandarna) {
      this.anvandarna = anvandarna;
    }

    @Override
    public Boolean visit(Arende ärende) {
      return anvandarna.contains(ärende.getHandläggare())
          || anvandarna.contains(ärende.getRegistrator())
          || anvandarna.contains(ärende.getSenasteModifierare())
          || anvandarna.contains(ärende.getÄgare());
    }

    @Override
    public Boolean visit(Atgard åtgärd) {
//      return anvandarna.contains(åtgärd.getHandläggare())
//          || anvandarna.contains(åtgärd.getRegistrator())
//          || anvandarna.contains(åtgärd.getSenasteModifierare())
//          || anvandarna.contains(åtgärd.getÄgare());

      return anvandarna.contains(åtgärd.getÄgare());
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
    final MatchesVisitor matcher = new MatchesVisitor(gatherAnvandare.getAnvandarna());
    List<FacetValue> values = new ArrayList<>(gatherAnvandare.getAnvandarna().size());
    for (Anvandare anvandare : gatherAnvandare.getAnvandarna()) {
      values.add(new FacetValue(searchResults, anvandare.getNamn()) {
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
