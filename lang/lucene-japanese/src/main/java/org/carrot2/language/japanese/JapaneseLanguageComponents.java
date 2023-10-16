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

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
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
        Tokenizer.class, () -> new LuceneAnalyzerTokenizerAdapter(new JapaneseAnalyzer()));
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
  }
}
