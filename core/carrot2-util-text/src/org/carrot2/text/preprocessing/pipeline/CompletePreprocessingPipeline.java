
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.text.linguistic.DefaultStemmerFactory;
import org.carrot2.text.linguistic.DefaultTokenizerFactory;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.linguistic.ITokenizerFactory;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.text.preprocessing.DocumentAssigner;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LanguageModelStemmer;
import org.carrot2.text.preprocessing.PhraseExtractor;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.StopListMarker;
import org.carrot2.text.preprocessing.Tokenizer;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * You should use {@link DefaultPreprocessingPipeline} to delegate preprocessing
 * to an appropriate implementation. 
 * 
 * <p>Performs a complete preprocessing on the provided documents. The preprocessing consists
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
public class CompletePreprocessingPipeline implements IPreprocessingPipeline
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
     * Tokenizer factory. Creates the tokenizers to be used by the clustering algorithm.
     */
    @Input
    @Init
    @Processing
    @Internal
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public ITokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();

    /**
     * Stemmer factory. Creates the stemmers to be used by the clustering algorithm.
     */
    @Input
    @Init
    @Processing
    @Internal
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public IStemmerFactory stemmerFactory = new DefaultStemmerFactory();

    /**
     * Lexical data factory. Creates the lexical data to be used by the clustering
     * algorithm, including stop word and stop label dictionaries.
     */
    @Input
    @Init
    @Processing
    @Internal
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public ILexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

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
        LanguageCode language, ContextRequired contextRequired)
    {
        final PreprocessingContext context = new PreprocessingContext(
            LanguageModel.create(language, stemmerFactory, tokenizerFactory,
                lexicalDataFactory), documents, query);

        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);

        if (contextRequired == ContextRequired.COMPLETE)
        {
            phraseExtractor.extractPhrases(context);
            labelFilterProcessor.process(context);
            documentAssigner.assign(context);
        }

        context.preprocessingFinished();
        return context;
    }
}
