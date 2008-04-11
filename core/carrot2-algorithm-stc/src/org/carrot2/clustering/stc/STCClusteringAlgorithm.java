package org.carrot2.clustering.stc;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.text.analysis.*;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * Suffix Tree Clustering (STC) algorithm.
 * 
 * @label STC Clustering
 */
@Bindable
public final class STCClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    Collection<Document> documents = Collections.<Document> emptyList();

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    Collection<Cluster> clusters = null;

    /**
     * Temporary tokenizer.
     * 
     * TODO: This should really be replaced with a strategy that splits document(s) into
     * sentences.
     */
    @Init
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        ExtendedWhitespaceAnalyzer.class
    })
    private ExtendedWhitespaceAnalyzer analyzer = new ExtendedWhitespaceAnalyzer();

    /**
     * {@link #documents} converted to an array.
     */
    private Document [] documentArray;

    /**
     * Performs STC clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        documentArray = this.documents.toArray(new Document [this.documents.size()]);

        final STCParameters params = new STCParameters();
        final STCEngine engine = new STCEngine();

        /*
         * Step 1: Tokenize input documents and perform shallow linguistic preprocessing
         * if needed (stemming, character case normalization).
         */
        final List<StemmedTerm []> documentData = prepareInputData();

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
        int max = params.getMaxClusters();

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
     * 
     */
    private List<StemmedTerm []> prepareInputData()
    {
        final ArrayList<StemmedTerm []> documentData = new ArrayList<StemmedTerm []>(
            documentArray.length);
        final ArrayList<StemmedTerm> currentDocument = new ArrayList<StemmedTerm>();
        try
        {
            ArrayList<String> fieldValues = new ArrayList<String>();
            for (final Document doc : documentArray)
            {
                final String title = doc.getField(Document.TITLE);
                if (!StringUtils.isEmpty(title)) fieldValues.add(title);

                final String snippet = doc.getField(Document.SUMMARY);
                if (!StringUtils.isEmpty(snippet)) fieldValues.add(snippet);

                TokenType type = null;
                Token t = null;
                StemmedTerm lastValue = null;
                while (!fieldValues.isEmpty())
                {
                    final Tokenizer ts = (ExtendedWhitespaceTokenizer) analyzer
                        .reusableTokenStream(null, new StringReader(fieldValues.remove(0)));

                    while ((t = ts.next(t)) != null)
                    {
                        // Add artificial marker separating sentences.
                        type = (TokenType) t.getPayload();
                        if (TokenTypeUtils.isSentenceDelimiter(type))
                        {
                            if (lastValue != null)
                            {
                                currentDocument.add(null);
                                lastValue = null;
                            }
                            continue;
                        }

                        final String termText = new String(t.termBuffer(), 0, t
                            .termLength());
                        final String stemmed = null;
                        final boolean stopword = false;
                        lastValue = new StemmedTerm(termText, stemmed, stopword);
                        currentDocument.add(lastValue);
                    }

                    // Split fields with a sentence break.
                    if (!fieldValues.isEmpty() && lastValue != null)
                    {
                        currentDocument.add(null);
                    }
                }

                documentData.add(currentDocument.toArray(new StemmedTerm [currentDocument
                    .size()]));
                currentDocument.clear();
            }
        }
        catch (IOException e)
        {
            throw ExceptionUtils.wrapAs(ProcessingException.class, e);
        }

        return documentData;
    }
}
