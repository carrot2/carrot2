package org.carrot2.clustering.stc;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.text.analysis.*;
import org.carrot2.text.preprocessing.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;

/**
 * Suffix Tree Clustering (STC) algorithm.
 * 
 * @label STC Clustering
 */
@Bindable
public final class STCClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    /**
     * Input documents.
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    public Collection<Document> documents = Collections.<Document> emptyList();

    /**
     * Output clusters.
     */
    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    public Collection<Cluster> clusters = null;

    /**
     * Preprocessing pipeline. Not an attribute, but contains bindable
     * attributes inside.
     */
    public Preprocessor preprocessor = new Preprocessor();

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

        final PreprocessingContext context = new PreprocessingContext();

        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING,
            PreprocessingTasks.MARK_TOKENS_STOPLIST);

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
            .newArrayListWithCapacity(documents.size());
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
                final int [] tokens = context.allTokens;
                final CharSequence [] images = context.allTokenImages;
                final int [] stemsMap = context.allTokensStemmed;
                final boolean [] commonWords = context.commonTermFlag;

                for (int i = start; i < start + length; i++)
                {
                    final int tokenCode = tokens[i];
                    final String term = images[tokenCode].toString();
                    final String stem = images[stemsMap[i]].toString();

                    boolean stop = commonWords[stemsMap[i]]
                        || TokenTypeUtils.maskType(context.allTypes[i]) == TokenType.TT_PUNCTUATION;

                    currentDocument.add(new StemmedTerm(term, stem, stop));
                }
                currentDocument.add(null);
            }
        };

        scanner.iterate(context);

        return documentData;
    }
}
