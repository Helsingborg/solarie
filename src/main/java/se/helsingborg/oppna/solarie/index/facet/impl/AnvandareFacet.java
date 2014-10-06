package se.helsingborg.oppna.solarie.index.facet.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.json.JSONException;
import org.json.JSONTokener;
import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetDefinition;
import se.helsingborg.oppna.solarie.index.facet.FacetValue;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-10-03 20:12
 */
public class AnvandareFacet extends FacetDefinition {

  public AnvandareFacet() {
  }

  @Override
  public void addFields(Document document, Indexable indexable) {
    GatherAnvandare gatherAnvandare = new GatherAnvandare();
    indexable.accept(gatherAnvandare);
    for (Anvandare användare : gatherAnvandare.getAnvändarna()) {
      document.add(new StringField("facet facetVaue", valueFactory(användare), Field.Store.NO));
    }
  }


  @Override
  public Facet facetFactory() {
    return new Facet("Användare") {
      @Override
      protected List<FacetValue> valuesFactory(List<SearchResult> searchResults) {
        GatherAnvandare gatherAnvandare = new GatherAnvandare();
        for (SearchResult searchResult : searchResults) {
          searchResult.getInstance().accept(gatherAnvandare);
        }

        Set<String> facetValues = new HashSet<>(gatherAnvandare.getAnvändarna().size());
        for (Anvandare anvandare : gatherAnvandare.getAnvändarna()) {
          facetValues.add(valueFactory(anvandare));
        }

        List<FacetValue> values = new ArrayList<>(gatherAnvandare.getAnvändarna().size());
        for (final String facetValue : facetValues) {

          final MatchesVisitor matcher = new MatchesVisitor(facetValue);

          values.add(new FacetValue(searchResults, facetValue) {

            @Override
            public boolean matches(SearchResult searchResult) {
              return searchResult.getInstance().accept(matcher);
            }

            @Override
            public JSONObject toJSON() throws JSONException {
              JSONObject facetValueJSON = super.toJSON();
              facetValueJSON.put("query", new JSONObject(new JSONTokener("{ 'type': 'term', 'field': 'facet facetVaue', 'value': '" + facetValue + "' }")));
              return facetValueJSON;

            }

          });
        }
        return values;
      }
    };
  }


  private class GatherAnvandare extends IndexableVisitor<Void> {
    private Set<Anvandare> användarna = new HashSet<Anvandare>(){
      @Override
      public boolean add(Anvandare anvandare) {
        if (anvandare == null) {
          return false;
        }
        return super.add(anvandare);
      }
    };

    @Override
    public Void visit(Arende ärende) {
        användarna.add(ärende.getHandläggare());
        användarna.add(ärende.getRegistrator());
        användarna.add(ärende.getSenasteModifierare());
        användarna.add(ärende.getÄgare());
      return null;
    }

    @Override
    public Void visit(Atgard åtgärd) {
        användarna.add(åtgärd.getÄgare());
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

  private class MatchesVisitor extends IndexableVisitor<Boolean> {

    private String facetVaue;

    private MatchesVisitor(String facetVaue) {
      this.facetVaue = facetVaue;
    }

    @Override
    public Boolean visit(Arende ärende) {
      return facetVaue.equals(valueFactory(ärende.getHandläggare()))
          || facetVaue.equals(valueFactory(ärende.getRegistrator()))
          || facetVaue.equals(valueFactory(ärende.getSenasteModifierare()))
          || facetVaue.equals(valueFactory(ärende.getÄgare()));
    }

    @Override
    public Boolean visit(Atgard åtgärd) {
      return facetVaue.equals(valueFactory(åtgärd.getÄgare()));
    }

    @Override
    public Boolean visit(Dokument dokument) {
      throw new UnsupportedOperationException();
    }
  }

  private String valueFactory(Anvandare anvandare) {
    if (anvandare == null) {
      return null;
    }
    return anvandare.getNamn() == null ? anvandare.getSignatur() : anvandare.getNamn();
  }



}
