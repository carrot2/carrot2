
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

import org.carrot2.clustering.Document;
import org.carrot2.language.LanguageComponents;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;

import java.util.stream.Stream;

/**
 * Performs a complete preprocessing on the provided documents. The preprocessing consists
 * of the following steps:
 * <ol>
 * <li>{@link InputTokenizer}</li>
 * <li>{@link CaseNormalizer}</li>
 * <li>{@link LanguageModelStemmer}</li>
 * <li>{@link StopListMarker}</li>
 * <li>{@link PhraseExtractor}</li>
 * <li>{@link LabelFilterProcessor}</li>
 * <li>{@link DocumentAssigner}</li>
 * </ol>
 */
public class CompletePreprocessingPipeline extends BasicPreprocessingPipeline {
  /**
   * Phrase Document Frequency threshold. Phrases appearing in fewer than
   * <code>dfThreshold</code> documents will be ignored.
   */
  public final AttrInteger phraseDfThreshold =
      attributes.register("phraseDfThreshold", AttrInteger.builder()
          .min(1)
          .max(100)
          .label("Phrase document frequency threshold")
          .defaultValue(1)
          .build());

  /**
   * Label filter processor used by the algorithm, contains bindable attributes.
   */
  public final AttrObject<LabelFilterProcessor> labelFilterProcessor = attributes.register("labelFilterProcessor",
      AttrObject.builder(LabelFilterProcessor.class)
          .defaultValue(new LabelFilterProcessor())
          .build());

  /**
   * Document assigner used by the algorithm, contains bindable attributes.
   */
  public final AttrObject<DocumentAssigner> documentAssigner = attributes.register("documentAssigner",
      AttrObject.builder(DocumentAssigner.class)
        .defaultValue(new DocumentAssigner())
        .build());

  public PreprocessingContext preprocess(Stream<? extends Document> documents, String query, LanguageComponents langModel) {
    try (PreprocessingContext context = new PreprocessingContext(langModel)) {
      tokenizer.get().tokenize(context, documents);
      caseNormalizer.normalize(context, wordDfThreshold.get());
      stemming.stem(context, query);
      stopListMarker.mark(context);
      new PhraseExtractor(phraseDfThreshold.get()).extractPhrases(context);
      labelFilterProcessor.get().process(context);
      documentAssigner.get().assign(context);
      return context;
    }
  }
}
