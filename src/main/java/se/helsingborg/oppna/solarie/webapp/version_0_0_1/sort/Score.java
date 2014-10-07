package se.helsingborg.oppna.solarie.webapp.version_0_0_1.sort;

import se.helsingborg.oppna.solarie.index.SearchResult;

import java.util.Comparator;

/**
 * @author kalle
 * @since 2014-10-06 21:11
 */
public class Score implements Comparator<SearchResult> {
  @Override
  public int compare(SearchResult o1, SearchResult o2) {
    if (o1.getScore() != null && o2.getScore() != null) {
      return o2.getScore().compareTo(o1.getScore());
    } else if (o1.getScore() == null && o2.getScore() == null) {
      return 0;
    } else if (o1.getScore() != null) {
      return -1;
    } else {
      return 1;
    }
  }
}