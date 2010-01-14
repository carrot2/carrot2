/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * Performs basic preprocessing steps on the provided documents. The {@link #preprocess(List, String)} method
 * applies the following preprocessing steps:
 * <ol>
 * <li>{@link Tokenizer#tokenize(PreprocessingContext)}</li>
 * <li>{@link CaseNormalizer#normalize(PreprocessingContext)}</li>
 * <li>{@link LanguageModelStemmer#stem(PreprocessingContext)}</li>
 * <li>{@link StopListMarker#mark(PreprocessingContext)}</li>
 * </ol>
 */
@Bindable(prefix = "PreprocessingPipeline")
public class BasicPreprocessingPipeline
{
    /**
     * Tokenizer used by the algorithm, contains bindable attributes.
     */
    public final Tokenizer tokenizer = new Tokenizer();

    /**
     * Case normalizer used by the algorithm, contains bindable attributes.
     */
    public final CaseNormalizer caseNormalizer = new CaseNormalizer();

    /**
     * Stemmer used by the algorithm, contains bindable attributes.
     */
    public final LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();

    /**
     * Stop list marker used by the algorithm, contains bindable attributes.
     */
    public final StopListMarker stopListMarker = new StopListMarker();

    /**
     * Language model factory used by the algorithm, contains bindable attributes.
     */
    @Input
    @Init
    @Processing
    @Attribute
    @ImplementingClasses(classes =
    {
        DefaultLanguageModelFactory.class
    }, strict = false)
    public ILanguageModelFactory languageModelFactory = new DefaultLanguageModelFactory();

    /**
     * Performs preprocessing on the provided list of documents. Results can be obtained
     * from the returned {@link PreprocessingContext}.
     */
    public PreprocessingContext preprocess(List<Document> documents, String query)
    {
        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getDefaultLanguageModel(), documents, query);
        preprocess(context);
        return context;
    }

    /**
     * Performs preprocessing on the provided {@link PreprocessingContext}.
     */
    public void preprocess(PreprocessingContext context)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);

        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
    }
}
