package se.helsingborg.oppna.solarie.index.analysis;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Split tokens on whitespace or dash.
 *
 * 'Ad-hoc' -> ['Ad', 'hoc'].
 * 'Damm sugare' -> ['Damm', 'sugare']
 *
 * @author kalle
 * @since 2014-09-17 02:24
 */
public class SwedishSarskrivningTextTokenizer extends CharTokenizer {

  /**
   * Construct a new SwedishSarskrivningTextTokenizer.
   * @param matchVersion Lucene version
   * to match See {@link <a href="#version">above</a>}
   *
   * @param in the input to split up into tokens
   */
  public SwedishSarskrivningTextTokenizer(Version matchVersion, Reader in) {
    super(matchVersion, in);
  }

  /**
   * Construct a new SwedishSarskrivningTextTokenizer using a given
   * {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.
   *
   * @param matchVersion Lucene version to match See
   *                     {@link <a href="#version">above</a>}
   * @param factory      the attribute factory to use for this {@link org.apache.lucene.analysis.Tokenizer}
   * @param in           the input to split up into tokens
   */
  public SwedishSarskrivningTextTokenizer(Version matchVersion, AttributeFactory factory, Reader in) {
    super(matchVersion, factory, in);
  }

  /**
   * Collects only characters which do not satisfy
   * {@link Character#isWhitespace(int)}.
   */
  @Override
  protected boolean isTokenChar(int c) {
    return !(Character.isWhitespace(c) || c == '-');
  }
}