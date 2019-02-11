
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
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.preprocessing.*;

import java.util.stream.Stream;

/**
 * Performs a complete preprocessing on the provided documents. The preprocessing consists
 * of the following steps:
 * <ol>
 * <li>{@link Tokenizer}</li>
 * <li>{@link CaseNormalizer}</li>
 * <li>{@link LanguageModelStemmer}</li>
 * <li>{@link StopListMarker}</li>
 * <li>{@link PhraseExtractor}</li>
 * <li>{@link LabelFilterProcessor}</li>
 * <li>{@link DocumentAssigner}</li>
 * </ol>
 */
public class CompletePreprocessingPipeline
{
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

    public PreprocessingContext preprocess(Stream<Document> documents,
                                           ITokenizer tokenizerImpl,
                                           IStemmer stemmer,
                                           ILexicalData lexicalData) {
        final PreprocessingContext context = new PreprocessingContext();

        tokenizer.tokenize(documents, context, tokenizerImpl);

        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context, stemmer);
        stopListMarker.mark(context, lexicalData);
        phraseExtractor.extractPhrases(context);
        labelFilterProcessor.process(context, lexicalData);
        documentAssigner.assign(context);

        context.preprocessingFinished();
        return context;
    }
}
