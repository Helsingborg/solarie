package se.helsingborg.oppna.solarie.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import se.helsingborg.oppna.solarie.index.analysis.SwedishSarskrivningTextAnalyzer;
import se.helsingborg.oppna.solarie.index.analysis.SwedishStemmedTextAnalyzer;
import se.helsingborg.oppna.solarie.index.analysis.SwedishTextAnalyzer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-10-03 08:13
 */
public class SolarieAnalyzerFactory {

  public static Analyzer factory() {
    Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
    fieldAnalyzers.put(SolarieFields.diarienummer, new KeywordAnalyzer());

    fieldAnalyzers.put(SolarieFields.ärende_mening, new SwedishTextAnalyzer());

    fieldAnalyzers.put(SolarieFields.åtgärd_text, new SwedishTextAnalyzer());

    fieldAnalyzers.put(SolarieFields.text, new SwedishTextAnalyzer());
    fieldAnalyzers.put(SolarieFields.text_singular, new SwedishStemmedTextAnalyzer());
    fieldAnalyzers.put(SolarieFields.text_särskivning, new SwedishSarskrivningTextAnalyzer());

    return new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), fieldAnalyzers);
  }


}
