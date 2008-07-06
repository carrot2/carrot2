package org.carrot2.clustering.lingo;

import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.linguistic.SnowballLanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;

/**
 * Lingo clustering algorithm.
 */
@Bindable
public class LingoClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    /**
     * {@link Document}s to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    /**
     * {@link Cluster}s created by the algorithm.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters = null;

    /**
     * Tokenizer used by the algorithm, contains bindable attributes.
     */
    public Tokenizer tokenizer = new Tokenizer();

    /**
     * Case normalizer used by the algorithm, contains bindable attributes.
     */
    public CaseNormalizer caseNormalizer = new CaseNormalizer();

    /**
     * Stemmer used by the algorithm, contains bindable attributes.
     */
    public LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();

    /**
     * Stop list marker used by the algorithm, contains bindable attributes.
     */
    public StopListMarker stopListMarker = new StopListMarker();

    /**
     * Phrase extractor used by the algorithm, contains bindable attributes.
     */
    public PhraseExtractor phraseExtractor = new PhraseExtractor();

    /**
     * Label filter processor used by the algorithm, contains bindable attributes.
     */
    public LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

    /**
     * Language model factory used by the algorithm, contains bindable attributes.
     */
    public LanguageModelFactory languageModelFactory = new SnowballLanguageModelFactory();

    /**
     * Performs Lingo clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        // Preprocessing of documents
        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getCurrentLanguage(), documents);
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
        phraseExtractor.extractPhrases(context);
        labelFilterProcessor.process(context);

        clusters = Lists.newArrayList();

        final int [][] phrasesWordIndices = context.allPhrases.wordIndices;
        final int [][] phrasesTfByDocument = context.allPhrases.tfByDocument;
        final char [][] wordsImage = context.allWords.image;
        for (int phraseIndex = 0; phraseIndex < phrasesWordIndices.length
            && phraseIndex < 10; phraseIndex++)
        {
            final Cluster cluster = new Cluster();
            final StringBuilder label = new StringBuilder();

            for (int wordIndex = 0; wordIndex < phrasesWordIndices[phraseIndex].length; wordIndex++)
            {
                label.append(wordsImage[phrasesWordIndices[phraseIndex][wordIndex]]);
                if (wordIndex < phrasesWordIndices[phraseIndex].length - 1)
                {
                    label.append(" ");
                }
            }

            cluster.addPhrases(label.toString());

            for (int j = 0; j < phrasesTfByDocument[phraseIndex].length / 2; j++)
            {
                cluster.addDocuments(documents
                    .get(phrasesTfByDocument[phraseIndex][j * 2]));
            }
            
            clusters.add(cluster);
        }
    }
}
