/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.stream.Stream;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;

/**
 * Performs a complete preprocessing on the provided documents. The preprocessing consists of the
 * following steps:
 *
 * <ol>
 *   <li>{@link InputTokenizer}
 *   <li>{@link CaseNormalizer}
 *   <li>{@link LanguageModelStemmer}
 *   <li>{@link StopListMarker}
 *   <li>{@link PhraseExtractor}
 *   <li>{@link LabelFilterProcessor}
 *   <li>{@link DocumentAssigner}
 * </ol>
 */
public class CompletePreprocessingPipeline extends BasicPreprocessingPipeline {
  /**
   * Phrase Document Frequency threshold. Phrases appearing in fewer than <code>dfThreshold</code>
   * documents will be ignored.
   */
  public final AttrInteger phraseDfThreshold =
      attributes.register(
          "phraseDfThreshold",
          AttrInteger.builder()
              .min(1)
              .max(100)
              .label("Phrase document frequency threshold")
              .defaultValue(1));

  /** Label filtering is a composite of individual filters. */
  public LabelFilterProcessor labelFilters = new LabelFilterProcessor();

  {
    attributes.register(
        "labelFilters",
        AttrObject.builder(LabelFilterProcessor.class)
            .label("Cluster label filters")
            .getset(() -> labelFilters, (v) -> labelFilters = v)
            .defaultValue(LabelFilterProcessor::new));
  }

  /** Document assigner used by the algorithm, contains bindable attributes. */
  public DocumentAssigner documentAssigner;

  {
    attributes.register(
        "documentAssigner",
        AttrObject.builder(DocumentAssigner.class)
            .label("Control over cluster-document assignment")
            .getset(() -> documentAssigner, (v) -> documentAssigner = v)
            .defaultValue(DocumentAssigner::new));
  }

  public PreprocessingContext preprocess(
      Stream<? extends Document> documents, String query, LanguageComponents langModel) {
    try (PreprocessingContext context = new PreprocessingContext(langModel)) {
      tokenizer.tokenize(context, documents);
      caseNormalizer.normalize(context, wordDfThreshold.get());
      stemming.stem(context, query);
      stopListMarker.mark(context);
      new PhraseExtractor(phraseDfThreshold.get()).extractPhrases(context);
      labelFilters.process(context);
      documentAssigner.assign(context);
      return context;
    }
  }
}
