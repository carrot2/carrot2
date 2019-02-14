
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

package org.carrot2.text.preprocessing.pipeline;

import org.carrot2.core.Document;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attrs.AttrComposite;
import org.carrot2.util.attrs.AttrInteger;
import org.carrot2.util.attrs.AttrObject;

import java.util.List;

/**
 * Performs basic preprocessing steps on the provided documents. The preprocessing
 * consists of the following steps:
 * <ol>
 * <li>{@link Tokenizer}</li>
 * <li>{@link CaseNormalizer}</li>
 * <li>{@link LanguageModelStemmer}</li>
 * <li>{@link StopListMarker}</li>
 * </ol>
 */
@Bindable
public class BasicPreprocessingPipeline extends AttrComposite implements IPreprocessingPipeline
{
    /**
     * Word Document Frequency threshold. Words appearing in fewer than
     * <code>dfThreshold</code> documents will be ignored.
     */
    public final AttrInteger dfThreshold =
        attributes.register("dfThreshold", AttrInteger.builder()
            .min(1)
            .max(100)
            .label("Word document frequency threshold")
            .defaultValue(1)
            .build());

    /**
     * Tokenizer used by the algorithm, contains bindable attributes.
     */
    public final AttrObject<Tokenizer> tokenizer =
        attributes.register("tokenizer", AttrObject.builder(Tokenizer.class)
            .defaultValue(new Tokenizer())
            .build());

    /**
     * Case normalizer used by the algorithm.
     */
    protected final CaseNormalizer caseNormalizer = new CaseNormalizer();

    /**
     * Stemmer used by the algorithm.
     */
    protected final LanguageModelStemmer stemming = new LanguageModelStemmer();

    /**
     * Stop list marker used by the algorithm, contains bindable attributes.
     */
    protected final StopListMarker stopListMarker = new StopListMarker();

    /**
     * Performs preprocessing on the provided list of documents. Results can be obtained
     * from the returned {@link PreprocessingContext}.
     */
    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageModel langModel)
    {
        try (PreprocessingContext context = new PreprocessingContext(langModel)) {
            tokenizer.get().tokenize(context, documents.iterator());
            caseNormalizer.normalize(context, dfThreshold.get());
            stemming.stem(context, query);
            stopListMarker.mark(context);
            return context;
        }
    }
}
