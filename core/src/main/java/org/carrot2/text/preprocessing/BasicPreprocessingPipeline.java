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
package org.carrot2.text.preprocessing;

import java.util.stream.Stream;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;

/**
 * Performs basic preprocessing steps on the provided documents. The preprocessing consists of the
 * following steps:
 *
 * <ol>
 *   <li>{@link InputTokenizer}
 *   <li>{@link CaseNormalizer}
 *   <li>{@link LanguageModelStemmer}
 *   <li>{@link StopListMarker}
 * </ol>
 */
public class BasicPreprocessingPipeline extends AttrComposite implements ContextPreprocessor {
  /**
   * Word Document Frequency threshold. Words appearing in fewer than <code>dfThreshold</code>
   * documents will be ignored.
   */
  public final AttrInteger wordDfThreshold =
      attributes.register(
          "wordDfThreshold",
          AttrInteger.builder()
              .min(1)
              .max(100)
              .label("Word document frequency threshold")
              .defaultValue(1));

  /** Case normalizer used by the algorithm. */
  protected final CaseNormalizer caseNormalizer = new CaseNormalizer();

  /** Stemmer used by the algorithm. */
  protected final LanguageModelStemmer stemming = new LanguageModelStemmer();

  /** Stop list marker used by the algorithm, contains modifiable parameters. */
  protected final StopListMarker stopListMarker = new StopListMarker();

  /** Tokenizer used by the algorithm. */
  protected final InputTokenizer tokenizer = new InputTokenizer();

  /**
   * Performs preprocessing on the provided list of documents. Results can be obtained from the
   * returned {@link PreprocessingContext}.
   */
  public PreprocessingContext preprocess(
      Stream<? extends Document> documents, String query, LanguageComponents langModel) {
    try (PreprocessingContext context = new PreprocessingContext(langModel)) {
      tokenizer.tokenize(context, documents);
      caseNormalizer.normalize(context, wordDfThreshold.get());
      stemming.stem(context, query);
      stopListMarker.mark(context);
      return context;
    }
  }
}
