package org.carrot2.clustering.lingo;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.matrix.NNIInterface;
import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.linguistic.SnowballLanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

import bak.pcj.IntIterator;
import bak.pcj.set.IntSet;

import com.google.common.collect.Lists;

/**
 * Lingo clustering algorithm.
 */
@Bindable(prefix = "LingoClusteringAlgorithm")
public class LingoClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    /**
     * Query that produced the documents. The query will help the algorithm to create
     * better clusters. Therefore, providing the query is optional but desirable.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY)
    public String query = null;

    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters = null;

    /**
     * Term weighting. The method for calculating weight of words in the term-document
     * matrices.
     * 
     * @level Advanced
     * @group Matrix model
     */
    @Input
    @Processing
    @Attribute
    @Required
    @ImplementingClasses(classes =
    {
        LogTfIdfTermWeighting.class, LinearTfIdfTermWeighting.class,
        TfTermWeighting.class
    }, strict = false)
    public TermWeighting termWeighting = new LogTfIdfTermWeighting();

    /**
     * Indicates whether Lingo used fast native matrix computation routines. Value of this
     * attribute is equal to {@link NNIInterface#isNativeBlasAvailable()} at the time of
     * running the algorithm.
     */
    @Processing
    @Output
    @Attribute
    public boolean nativeMatrixUsed;

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
     * Document assigner used by the algorithm, contains bindable attributes.
     */
    public DocumentAssigner documentAssigner = new DocumentAssigner();

    /**
     * Language model factory used by the algorithm, contains bindable attributes.
     */
    public LanguageModelFactory languageModelFactory = new SnowballLanguageModelFactory();

    /**
     * Term-document matrix builder for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    /**
     * Term-document matrix reducer for the algorithm, contains bindable attributes.
     */
    public TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    /**
     * Cluster label builder, contains bindable attributes.
     */
    public ClusterBuilder clusterBuilder = new ClusterBuilder();

    /**
     * Cluster label formatter, contains bindable attributes.
     */
    public LabelFormatter labelFormatter = new LabelFormatter();

    private static final Logger log = Logger.getLogger(LingoClusteringAlgorithm.class);

    @Override
    public void init(ControllerContext context)
    {
        if (NNIInterface.isNativeBlasAvailable())
        {
            log.info("Native BLAS routines available");
        }
        else
        {
            log.info("Native BLAS routines not available");
        }
    }

    /**
     * Performs Lingo clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        nativeMatrixUsed = NNIInterface.isNativeBlasAvailable();

        // Preprocessing of documents
        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getCurrentLanguage(), documents, query);
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
        phraseExtractor.extractPhrases(context);
        labelFilterProcessor.process(context);
        documentAssigner.assign(context);

        // Term-document matrix building and reduction
        LingoProcessingContext lingoContext = new LingoProcessingContext(context);
        matrixBuilder.build(lingoContext, termWeighting);
        matrixReducer.reduce(lingoContext);

        // Cluster label building
        clusterBuilder.buildLabels(lingoContext, termWeighting);

        // Document assignment
        clusterBuilder.assignDocuments(lingoContext, termWeighting);

        // Cluster merging
        clusterBuilder.merge(lingoContext);

        // Format final clusters
        clusters = Lists.newArrayList();
        final int [] clusterLabelIndex = lingoContext.clusterLabelFeatureIndex;
        final IntSet [] clusterDocuments = lingoContext.clusterDocuments;
        final double [] clusterLabelScore = lingoContext.clusterLabelScore;
        for (int i = 0; i < clusterLabelIndex.length; i++)
        {
            final Cluster cluster = new Cluster();

            final int labelFeature = clusterLabelIndex[i];
            if (labelFeature < 0)
            {
                // Cluster removed during merging
                continue;
            }

            // Add label and score
            cluster.addPhrases(labelFormatter.format(context, labelFeature));
            cluster.setAttribute(Cluster.SCORE, clusterLabelScore[i]);

            // Add documents
            for (IntIterator it = clusterDocuments[i].iterator(); it.hasNext();)
            {
                cluster.addDocuments(documents.get(it.next()));
            }

            // Add cluster
            if (cluster.getDocuments().size() > 1)
            {
                clusters.add(cluster);
            }
        }

        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
        Cluster.appendOtherTopics(documents, clusters);
    }
}
