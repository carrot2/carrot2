
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

package org.carrot2.core.benchmarks.memtime;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.clustering.IMonolingualClusteringAlgorithm;
import org.carrot2.text.clustering.MultilingualClustering;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;

/**
 * This class simulates running {@link CompletePreprocessingPipeline} and
 * {@link MultilingualClustering} only, no clustering is performed.
 */
@Bindable(prefix = "CompletePreprocessing")
public final class CompletePreprocessing extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
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
     * Common preprocessing tasks handler.
     */
    public CompletePreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();

    /**
     * A helper for performing multilingual clustering.
     */
    public MultilingualClustering multilingualClustering = new MultilingualClustering();

    /**
     * Performs STC clustering of {@link #documents}.
     */
    @Override
    public void process() throws ProcessingException
    {
        final List<Document> originalDocuments = documents;
        clusters = multilingualClustering.process(documents,
            new IMonolingualClusteringAlgorithm()
            {
                public List<Cluster> process(List<Document> documents,
                    LanguageCode language)
                {
                    CompletePreprocessing.this.documents = documents;
                    CompletePreprocessing.this.cluster(language);
                    return CompletePreprocessing.this.clusters;
                }
            });
        documents = originalDocuments;
    }

    /**
     * Performs the actual clustering with an assumption that all documents are written in
     * one <code>language</code>.
     */
    private void cluster(LanguageCode language)
    {
        clusters = new ArrayList<Cluster>();
        preprocessingPipeline.preprocess(documents, null, language);
    }
}
