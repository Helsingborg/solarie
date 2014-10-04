package se.helsingborg.oppna.solarie.index.facet;

import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.index.SearchResult;

import java.util.*;

/**
 * @author kalle
 * @since 2014-10-03 17:50
 */
public abstract interface FacetFactory {

  public abstract Facet factory();

}
