package se.helsingborg.oppna.solarie.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * @author kalle
 * @since 2014-09-17 01:58
 */
public class SwedishTextAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

    Version matchVersion = Version.LUCENE_45;

    final Tokenizer source = new StandardTokenizer(matchVersion, reader);
    TokenStream result = new StandardFilter(matchVersion, source);
    result = new LowerCaseFilter(matchVersion, result);
    return new TokenStreamComponents(source, result);


  }
}
