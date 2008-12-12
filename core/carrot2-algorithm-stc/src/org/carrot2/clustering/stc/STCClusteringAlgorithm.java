
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.text.analysis.*;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.carrot2.text.suffixtrees.Node;
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
    IClusteringAlgorithm
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
     * Documents to cluster.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    /**
     * Clusters created by the algorithm.
     */
    @Processing
    @Output
    @Internal
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
    public ILanguageModelFactory languageModelFactory = new DefaultLanguageModelFactory();

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
         * Step 1: Convert documents to "legacy" STC input. TODO: This should be
         * eventually converted to use the new suffix tree code. See:
         * http://issues.carrot2.org/browse/CARROT-389
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

            int maxPhr = params.maxPhrases;
            final List phrases = b.getDescriptionPhrases();
            for (Iterator j = phrases.iterator(); j.hasNext() && (maxPhr > 0); maxPhr--)
            {
                final Phrase p = (Phrase) j.next();

                final boolean [] stopwords = new boolean[p.getTerms().size()];
                final char [][] images = new char [stopwords.length][];

                Node.Phrase terms = p.getTerms();
                int oindex = 0;
                for (Iterator<Object> o = terms.iterator(); o.hasNext(); oindex++)
                {
                    final StemmedTerm t = (StemmedTerm) o.next();
                    stopwords[oindex] = t.stopword;
                    images[oindex] = t.getTerm().toCharArray();
                }

                newCluster.addPhrases(LabelFormatter.format(images, stopwords));
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
                        .maskType(context.allTokens.type[i]) == ITokenType.TT_PUNCTUATION;
                    final int wordIndex = tokenWordIndices[i];

                    if (wordIndex < 0 && !isPunctuation)
                    {
                        continue;
                    }

                    boolean stop = isPunctuation || wordIndex < 0;

                    /*
                     * Break sentences on punctuation, do not break them on stop words
                     * (trimming edge stopwords is performed later on).
                     */
                    if (stop)
                    {
                        currentDocument.add(null);
                    }
                    else
                    {
                        final String term = new String(wordImages[wordIndex]);
                        final String stem = new String(
                            stemImages[wordStemIndices[wordIndex]]);

                        stop = stop | commonWords[wordIndex];

                        currentDocument.add(new StemmedTerm(term, stem, stop));
                    }
                }
                currentDocument.add(null);
            }
        };

        scanner.iterate(context);

        return documentData;
    }
}
