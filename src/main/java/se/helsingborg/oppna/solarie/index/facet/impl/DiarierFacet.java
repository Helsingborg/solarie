package se.helsingborg.oppna.solarie.index.facet.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.json.JSONException;
import org.json.JSONTokener;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.domain.Indexable;
import se.helsingborg.oppna.solarie.index.SearchResult;
import se.helsingborg.oppna.solarie.index.facet.Facet;
import se.helsingborg.oppna.solarie.index.facet.FacetDefinition;
import se.helsingborg.oppna.solarie.index.facet.FacetValue;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarium;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-10-03 19:40
 */
public class DiarierFacet extends FacetDefinition {

  public DiarierFacet() {

  }

  @Override
  public Facet facetFactory() {
    return new Facet("Diarier"){
      @Override
      public List<FacetValue> valuesFactory(List<SearchResult> searchResults) {

        Set<Diarium> diarier = new HashSet<>();
        for (SearchResult searchResult : searchResults) {
          diarier.add(searchResult.getInstance().accept(GetDiarium.getInstance()));
        }
        List<FacetValue> facetValues = new ArrayList<>(diarier.size());
        for (final Diarium diarium : diarier) {
          facetValues.add(new FacetValue(searchResults, diarium.getNamn()) {

            @Override
            public boolean matches(SearchResult searchResult) {
              return searchResult.getInstance().accept(GetDiarium.getInstance()).equals(diarium);
            }

            @Override
            public JSONObject toJSON() throws JSONException {
              JSONObject facetValueJSON = super.toJSON();
              facetValueJSON.put("query", new JSONObject(new JSONTokener("{ 'type': 'term', 'field': 'facet diarier', 'value': '" + diarium.getIdentity() + "' }")));
              return facetValueJSON;

            }
          });
        }
        return facetValues;


      }
    };
  }

  @Override
  public void addFields(Document document, Indexable indexable) {
      document.add(new StringField("facet diarium", String.valueOf(indexable.accept(GetDiarium.getInstance()).getIdentity()), Field.Store.NO));
  }



}
