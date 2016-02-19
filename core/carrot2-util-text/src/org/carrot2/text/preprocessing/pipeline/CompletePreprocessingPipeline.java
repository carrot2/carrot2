
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.text.preprocessing.DocumentAssigner;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LanguageModelStemmer;
import org.carrot2.text.preprocessing.PhraseExtractor;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.StopListMarker;
import org.carrot2.text.preprocessing.Tokenizer;
import org.carrot2.util.attribute.Bindable;

/**
 * Performs a complete preprocessing on the provided documents. The preprocessing consists
 * of the following steps:
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
@Bindable(prefix = "PreprocessingPipeline")
public class CompletePreprocessingPipeline extends BasicPreprocessingPipeline
{
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

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query,
        LanguageCode language)
    {
        final PreprocessingContext context = new PreprocessingContext(
            LanguageModel.create(language, stemmerFactory, tokenizerFactory,
                lexicalDataFactory), documents, query);

        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
        phraseExtractor.extractPhrases(context);
        labelFilterProcessor.process(context);
        documentAssigner.assign(context);

        context.preprocessingFinished();
        return context;

    }
}
