package se.helsingborg.oppna.solarie.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * Joins 2-3 tokens separated by dash or whitespace to a single token.
 *
 * Damm sugare -> dammsugare
 *
 * @author kalle
 * @since 2014-09-17 01:58
 */
public class SwedishSarskrivningTextAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

    Version matchVersion = Version.LUCENE_45;

    final Tokenizer source = new SwedishSarskrivningTextTokenizer(matchVersion, reader);
    TokenStream result = new StandardFilter(matchVersion, source);
    result = new ShingleFilter(result, 2, 3);
    result = new LowerCaseFilter(matchVersion, result);
    return new TokenStreamComponents(source, result);


  }
}
