/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.japanese;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKWidthCharFilter;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.UserDictionary;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneAnalyzerTokenizerAdapter;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class JapaneseLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Japanese";

  public JapaneseLanguageComponents() {
    super("Carrot2 (Japanese via Apache Lucene components)", NAME);
    registerResourceless(Stemmer.class, () -> (word) -> null);
    registerResourceless(
        Tokenizer.class, () -> new LuceneAnalyzerTokenizerAdapter(new ExtendedJapaneseAnalyzer()));
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
  }

  /**
   * Extended JapaneseAnalyzer tuned specifically for text clustering/labeling. This would not work
   * well for arbitrary texts as it is, but is supposed to be an extension point.
   */
  static class ExtendedJapaneseAnalyzer extends StopwordAnalyzerBase {
    private static UserDictionary userDict;

    static {
      try (Reader reader =
          new InputStreamReader(
              JapaneseLanguageComponents.class
                  .getClassLoader()
                  .getResourceAsStream("org/carrot2/language/japanese/japanese.userdict.utf8"),
              StandardCharsets.UTF_8)) {
        userDict = UserDictionary.open(reader);
      } catch (IOException | NullPointerException e) {
        // shouldn't happen
        throw new RuntimeException("Cannot load user dictionary.", e);
      }
    }

    private final JapaneseTokenizer.Mode mode = JapaneseTokenizer.Mode.NORMAL;
    private final Set<String> stoptags = JapaneseAnalyzer.getDefaultStopTags();
    private final Set<String> keeptags = Set.of("名詞-");
    private final int minTokenLength = 2;
    private final int maxTokenLength = Integer.MAX_VALUE;

    public ExtendedJapaneseAnalyzer() {
      super(JapaneseAnalyzer.getDefaultStopSet());
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
      org.apache.lucene.analysis.Tokenizer tokenizer =
          new JapaneseTokenizer(userDict, true, false, mode);
      TokenStream stream = new JapaneseBaseFormFilter(tokenizer);
      stream = new JapanesePartOfSpeechKeepFilter(stream, keeptags);
      // stream = new JapanesePartOfSpeechStopFilter(stream, stoptags);

      stream = new LengthFilter(stream, minTokenLength, maxTokenLength);
      stream = new StopFilter(stream, stopwords);
      stream = new JapaneseKatakanaStemFilter(stream);
      stream = new LowerCaseFilter(stream);

      return new Analyzer.TokenStreamComponents(tokenizer, stream);
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
      return new CJKWidthCharFilter(reader);
    }
  }
}
