
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

package org.carrot2.text.preprocessing;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.util.attribute.Bindable;

/**
 * Performs common preprocessing steps, such as tokenization, stemming and phrase
 * extraction, on the provided documents. The {@link #preprocess(List, String)} method
 * applies the following preprocessing steps:
 * <ol>
 * <li>{@link Tokenizer#tokenize(PreprocessingContext)}</li>
 * <li>{@link CaseNormalizer#normalize(PreprocessingContext)}</li>
 * <li>{@link LanguageModelStemmer#stem(PreprocessingContext)}</li>
 * <li>{@link StopListMarker#mark(PreprocessingContext)}</li>
 * <li>{@link PhraseExtractor#extractPhrases(PreprocessingContext)}</li>
 * <li>{@link LabelFilterProcessor#process(PreprocessingContext)}</li>
 * <li>{@link DocumentAssigner#assign(PreprocessingContext)}</li>
 * </ol>
 */
@Bindable
public class PreprocessingPipeline
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
     * Phrase extractor used by the algorithm, contains bindable attributes.
     */
    public final PhraseExtractor phraseExtractor = new PhraseExtractor();

    /**
     * Label filter processor used by the algorithm, contains bindable attributes.
     */
    public final LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

    /**
     * Document assigner used by the algorithm, contains bindable attributes.
     */
    public final DocumentAssigner documentAssigner = new DocumentAssigner();

    /**
     * Language model factory used by the algorithm, contains bindable attributes.
     */
    public final ILanguageModelFactory languageModelFactory = new DefaultLanguageModelFactory();

    /**
     * Performs preprocessing on the provided list of documents. Results can be obtained
     * from the returned {@link PreprocessingContext}.
     */
    public PreprocessingContext preprocess(List<Document> documents, String query)
    {
        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getCurrentLanguage(), documents, query);
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
        phraseExtractor.extractPhrases(context);
        labelFilterProcessor.process(context);
        documentAssigner.assign(context);
    }
}
