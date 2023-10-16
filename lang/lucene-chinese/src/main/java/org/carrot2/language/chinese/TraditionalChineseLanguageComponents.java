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
package org.carrot2.language.chinese;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.icu.segmentation.DefaultICUTokenizerConfig;
import org.apache.lucene.analysis.icu.segmentation.ICUTokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneAnalyzerTokenizerAdapter;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class TraditionalChineseLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Chinese-Traditional";

  public TraditionalChineseLanguageComponents() {
    super("Carrot2 (Traditional Chinese via Apache Lucene components)", NAME);
    registerResourceless(Stemmer.class, () -> (word) -> null);
    registerResourceless(
        Tokenizer.class,
        () -> new LuceneAnalyzerTokenizerAdapter(new TraditionalChineseAnalyzer()));
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(""));
    registerDefaultLexicalData();
  }

  private static class TraditionalChineseAnalyzer extends Analyzer {
    protected TokenStreamComponents createComponents(String fieldName) {
      return new TokenStreamComponents(
          new ICUTokenizer(
              AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
              new DefaultICUTokenizerConfig(true, true)));
    }
  }
}
