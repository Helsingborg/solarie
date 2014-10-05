package se.helsingborg.oppna.solarie.index.facet.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.json.JSONException;
import org.json.JSONTokener;
import se.helsingborg.oppna.solarie.domain.Indexable;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetDefinition;
import se.helsingborg.oppna.solarie.index.facet.FacetValue;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarium;
import se.helsingborg.oppna.solarie.index.visitors.GetInkom;
import se.helsingborg.oppna.solarie.index.visitors.GetUtgick;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2014-10-03 19:40
 */
public class InkommetUtgaendeFacet extends FacetDefinition {

  private static String fieldName = "facet in/ut";

  public InkommetUtgaendeFacet() {

  }

  @Override
  public Facet facetFactory() {
    return new Facet("In/ut") {
      @Override
      public List<FacetValue> valuesFactory(List<SearchResult> searchResults) {

        FacetValue in = new FacetValue(searchResults, "Inkommande") {
          @Override
          public boolean matches(SearchResult searchResult) {
            return searchResult.getInstance().accept(GetInkom.getInstance()) != null;
          }

          @Override
          public JSONObject toJSON() throws JSONException {
            JSONObject facetValueJSON = super.toJSON();
            facetValueJSON.put("query", new JSONObject(new JSONTokener("{ 'type': 'term', 'field': '" + fieldName + "', 'value': 'in' }")));
            return facetValueJSON;
          }
        };

        FacetValue ut = new FacetValue(searchResults, "Utgående") {
          @Override
          public boolean matches(SearchResult searchResult) {
            return searchResult.getInstance().accept(GetInkom.getInstance()) != null;
          }

          @Override
          public JSONObject toJSON() throws JSONException {
            JSONObject facetValueJSON = super.toJSON();
            facetValueJSON.put("query", new JSONObject(new JSONTokener("{ 'type': 'term', 'field': '" + fieldName + "', 'value': 'ut' }")));
            return facetValueJSON;
          }
        };

        List<FacetValue> facetValues = new ArrayList<>(2);
        if (!in.getMatches().isEmpty()) {
          facetValues.add(in);
        }
        if (!ut.getMatches().isEmpty()) {
          facetValues.add(ut);
        }
        return facetValues;
      }
    };

  }

  @Override
  public void addFields(Document document, Indexable indexable) {
    if (indexable.accept(GetInkom.getInstance()) != null) {
      document.add(new StringField(fieldName, "in", Field.Store.NO));
    }
    if (indexable.accept(GetUtgick.getInstance()) != null) {
      document.add(new StringField(fieldName, "ut", Field.Store.NO));
    }
  }


}
