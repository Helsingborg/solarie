package se.helsingborg.oppna.solarie.index;

import org.apache.lucene.search.Explanation;
import se.helsingborg.oppna.solarie.domain.Indexable;

/**
 * @author kalle
 * @since 2014-10-03 03:11
 */
public class SearchResult {

  private Float score;
  private Float normalizedScore;
  private Indexable instance;
  private Explanation explanation;

  public Explanation getExplanation() {
    return explanation;
  }

  public void setExplanation(Explanation explanation) {
    this.explanation = explanation;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public Indexable getInstance() {
    return instance;
  }

  public void setInstance(Indexable instance) {
    this.instance = instance;
  }

  public Float getNormalizedScore() {
    return normalizedScore;
  }

  public void setNormalizedScore(Float normalizedScore) {
    this.normalizedScore = normalizedScore;
  }
}
