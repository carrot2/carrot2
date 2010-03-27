package org.carrot2.core.benchmarks.memtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;

/**
 * This class simulates running {@link BasicPreprocessingPipeline} and
 * {@link MultilingualClustering} only, no clustering is performed.
 */
@Bindable(prefix = "PreprocessingOnly")
public final class BasicPreprocessingOnly extends ProcessingComponentBase implements
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
    public BasicPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();

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
                    BasicPreprocessingOnly.this.documents = documents;
                    BasicPreprocessingOnly.this.cluster(language);
                    return BasicPreprocessingOnly.this.clusters;
                }
            });
        documents = originalDocuments;

        if (multilingualClustering.languageAggregationStrategy == LanguageAggregationStrategy.FLATTEN_ALL)
        {
            Collections.sort(clusters, new Comparator<Cluster>()
            {
                public int compare(Cluster c1, Cluster c2)
                {
                    if (c1.isOtherTopics()) return 1;
                    if (c2.isOtherTopics()) return -1;
                    if (c1.getScore() < c2.getScore()) return 1;
                    if (c1.getScore() > c2.getScore()) return -1;
                    if (c1.size() < c2.size()) return 1;
                    if (c1.size() > c2.size()) return -1;
                    return 0;
                }
            });
        }
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
