package org.carrot2.clustering.stc;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.text.analysis.*;
import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.linguistic.SnowballLanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;

/**
 * Suffix Tree Clustering (STC) algorithm.
 * 
 * @label STC Clustering
 */
@SuppressWarnings("unchecked")
@Bindable
public final class STCClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    /**
     * Query that produced the documents, optional.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY)
    public String query = null;

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
     * Language model factory used by the algorithm, contains bindable attributes.
     */
    public LanguageModelFactory languageModelFactory = new SnowballLanguageModelFactory();

    /**
     * Parameters and thresholds of the algorithm.
     */
    public STCClusteringParameters params = new STCClusteringParameters();

    /**
     * Performs STC clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        final STCEngine engine = new STCEngine();

        final PreprocessingContext context = new PreprocessingContext(
            languageModelFactory.getCurrentLanguage(), documents, null);
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        stopListMarker.mark(context);
        
        final Document [] documentArray = this.documents
            .toArray(new Document [this.documents.size()]);

        /*
         * Step 1: Convert documents to legacy STC input.
         */
        final List<StemmedTerm []> documentData = convertToLegacyFormat(context);

        /*
         * Step 2: Create a generalized suffix tree from phrases in the input.
         */
        engine.createSuffixTree(documentData
            .toArray(new StemmedTerm [documentData.size()] []));

        /*
         * Step 3: Create "base" clusters by looking in the generalized suffix tree and
         * selecting appropriate nodes.
         */
        engine.createBaseClusters(params);

        /*
         * Step 4: Merge base clusters that overlap too much to form final clusters.
         */
        engine.createMergedClusters(params);

        /*
         * Step 5: Post-process output and set output attributes.
         */
        clusters = new ArrayList<Cluster>();

        final List mergedClusters = engine.getClusters();
        int max = params.maxClusters;

        final HashSet<Document> junkDocuments = new HashSet<Document>(mergedClusters
            .size());
        junkDocuments.addAll(documents);

        for (Iterator i = mergedClusters.iterator(); i.hasNext() && (max > 0); max--)
        {
            final MergedCluster b = (MergedCluster) i.next();
            final Cluster newCluster = new Cluster();

            // TODO: This should be a configuration parameter?
            int maxPhr = 3;
            final List phrases = b.getDescriptionPhrases();
            for (Iterator j = phrases.iterator(); j.hasNext() && (maxPhr > 0); maxPhr--)
            {
                final Phrase p = (Phrase) j.next();
                newCluster.addPhrases(p.userFriendlyTerms().trim());
            }

            for (Iterator j = b.getDocuments().iterator(); j.hasNext();)
            {
                final int docIndex = ((Integer) j.next()).intValue();
                newCluster.addDocuments(documentArray[docIndex]);
                junkDocuments.remove(documentArray[docIndex]);
            }

            clusters.add(newCluster);
        }

        // Create the 'other topics' cluster.
        if (junkDocuments.size() > 0)
        {
            final Cluster newCluster = new Cluster();
            newCluster.setAttribute(Cluster.OTHER_TOPICS, true);
            newCluster.addPhrases(Cluster.OTHER_TOPICS);

            for (Document d : junkDocuments)
            {
                newCluster.addDocuments(d);
            }

            clusters.add(newCluster);
        }
    }

    /**
     * Convert preprocessed data to legacy data structures required by the STC.
     */
    private List<StemmedTerm []> convertToLegacyFormat(PreprocessingContext context)
    {
        final ArrayList<StemmedTerm []> documentData = Lists
            .newArrayListWithExpectedSize(documents.size());
        final ArrayList<StemmedTerm> currentDocument = Lists.newArrayList();

        final PreprocessedDocumentScanner scanner = new PreprocessedDocumentScanner()
        {
            protected void document(PreprocessingContext context, int start, int length)
            {
                super.document(context, start, length);

                documentData.add(currentDocument.toArray(new StemmedTerm [currentDocument
                    .size()]));
                currentDocument.clear();
            }

            protected void sentence(PreprocessingContext context, int start, int length)
            {
                final int [] tokenWordIndices = context.allTokens.wordIndex;
                final char [][] wordImages = context.allWords.image;
                final int [] wordStemIndices = context.allWords.stemIndex;
                final boolean [] commonWords = context.allWords.commonTermFlag;
                final char [][] stemImages = context.allStems.image;

                for (int i = start; i < start + length; i++)
                {
                    final boolean isPunctuation = TokenTypeUtils
                        .maskType(context.allTokens.type[i]) == TokenType.TT_PUNCTUATION;
                    final int wordIndex = tokenWordIndices[i];

                    if (wordIndex < 0 && !isPunctuation)
                    {
                        continue;
                    }

                    final String term = (wordIndex >= 0 ? new String(
                        wordImages[wordIndex]) : ".");
                    final String stem = (wordIndex >= 0 ? new String(
                        stemImages[wordStemIndices[wordIndex]]) : ".");

                    boolean stop = isPunctuation || commonWords[wordIndex];

                    currentDocument.add(new StemmedTerm(term, stem, stop));
                }
                currentDocument.add(null);
            }
        };

        scanner.iterate(context);

        return documentData;
    }
}
