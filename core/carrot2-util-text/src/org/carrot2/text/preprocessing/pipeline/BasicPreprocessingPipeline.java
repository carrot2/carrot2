
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.*;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.carrot2.text.linguistic.ExtendedLanguageModelFactory;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * Performs basic preprocessing steps on the provided documents. The
 * preprocessing consists of the following steps:
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
     * Language model factory. Creates language the language model to be used by the
     * clustering algorithm. The language models provides the lexical resources required
     * to perform clustering, including stop words and a word stemming algorithm.
     * 
     * @group Preprocessing
     * @level Advanced
     */
    @Input
    @Init
    @Processing
    @Internal
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
    public PreprocessingContext preprocess(List<Document> documents, String query,
        LanguageCode language)
    {
        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getLanguageModel(language), documents, query);
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
        context.preprocessingFinished();
    }
}
