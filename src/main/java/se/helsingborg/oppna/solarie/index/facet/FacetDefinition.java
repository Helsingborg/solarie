package se.helsingborg.oppna.solarie.index.facet;

import org.apache.lucene.document.Document;
import se.helsingborg.oppna.solarie.domain.Indexable;

/**
 * @author kalle
 * @since 2014-10-04 20:19
 */
public abstract class FacetDefinition {

  public abstract Facet facetFactory();

  public abstract void addFields(Document document, Indexable indexable);


}
