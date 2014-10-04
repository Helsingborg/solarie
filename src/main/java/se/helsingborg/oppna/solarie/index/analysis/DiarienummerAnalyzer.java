package se.helsingborg.oppna.solarie.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2014-09-17 01:58
 */
public class DiarienummerAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

    Version matchVersion = Version.LUCENE_45;

    final Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);
    TokenStream result = new StandardFilter(matchVersion, source);
    result = new DiarienummerTokenFilter(result);
    return new TokenStreamComponents(source, result);

  }

  private static Pattern fullPattern = Pattern.compile("([1-2][0-9]{4}):([0-9]+)");
  private static Pattern årPattern = Pattern.compile("[1-2][0-9]{4}");
  private static Pattern löpernummerPattern = Pattern.compile("[0-9]+");

  private class DiarienummerTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    private DiarienummerTokenFilter(TokenStream input) {
      super(input);
    }

    private LinkedList<char[]> queue = new LinkedList<>();

    @Override
    public boolean incrementToken() throws IOException {
      char[] bufferd = queue.poll();
      if (bufferd != null) {
        termAtt.copyBuffer(bufferd, 0, bufferd.length);
        return true;

      } else if (input.incrementToken()){

        String string = termAtt.toString();
        Matcher matcher = fullPattern.matcher(string);
        if (matcher.matches()) {
          queue.add(matcher.group(1).toCharArray());
          queue.add(matcher.group(2).toCharArray());
        } else {
          matcher = årPattern.matcher(string);
          if (matcher.matches()) {
            return true;
          } else {
            matcher = löpernummerPattern.matcher(string);
            if (matcher.matches()) {
              return true;
            }
          }
        }
      }

      return false;

    }


  }

}
