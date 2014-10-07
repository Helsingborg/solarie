package se.helsingborg.oppna.solarie.webapp.version_0_0_1.sort;

import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.SearchResult;

import java.util.Comparator;

/**
 * @author kalle
 * @since 2014-10-06 20:55
 */
public class Timestamp implements Comparator<SearchResult> {

  private IdentifiableVisitor<Long> getTimestamp = new IndexableVisitor<Long>() {
    @Override
    public Long visit(Arende ärende) {
      return ärende.getRegistrerad();
    }

    @Override
    public Long visit(Atgard åtgärd) {
      return åtgärd.getRegistrerad();
    }

    @Override
    public Long visit(Dokument dokument) {
      throw new UnsupportedOperationException();
    }
  };

  @Override
  public int compare(SearchResult o1, SearchResult o2) {
    Long t1 = o1.getInstance().accept(getTimestamp);
    Long t2 = o2.getInstance().accept(getTimestamp);

    if (t1 == null && t2 == null) {
      return 0;
    } else if (t1 != null && t2 != null) {
      return t2.compareTo(t1);
    } else if (t1 == null) {
      return 1;
    } else {
      return -1;
    }
  }
}

