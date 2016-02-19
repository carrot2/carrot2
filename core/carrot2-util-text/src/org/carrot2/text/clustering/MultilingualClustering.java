
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

package org.carrot2.text.clustering;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.*;
import org.carrot2.shaded.guava.common.collect.Multiset.Entry;

/**
 * A helper for clustering multilingual collections of documents. The helper partitions
 * the input documents by {@link org.carrot2.core.Document#LANGUAGE}, clusters each such monolingual
 * partition separately and then aggregates the partial cluster lists based on the
 * selected {@link LanguageAggregationStrategy}.
 */
@Bindable(prefix = "MultilingualClustering")
public class MultilingualClustering
{
    /** {@link Group} name. */
    private static final String MULTILINGUAL_CLUSTERING = "Multilingual clustering";

    /**
     * Defines how monolingual partial clusters will be combined to form final results.
     */
    public enum LanguageAggregationStrategy
    {
        /**
         * Combines clusters created for all languages into one flat list. In this
         * setting, the first level of the clusters hierarchy will contain labels in all
         * input languages.
         */
        FLATTEN_ALL("Flatten clusters from all languages"),

        /**
         * Puts clusters generated for the largest language partition on the first level
         * of the hierarchy. Clusters generated for the other languages are placed in the
         * "Other Languages" cluster appended at the end of the list.
         */
        FLATTEN_MAJOR_LANGUAGE("Flatten clusters from the majority language"),

        /**
         * Puts clusters corresponding to language names, e.g. English, German, Spanish,
         * on the first level of the hierarchy. Each such cluster contains the actual
         * clusters generated for documents in the corresponding language.
         */
        FLATTEN_NONE("Dedicated parent cluster for each language"),
        
        /**
         * Clusters all documents assuming the language of the majority of documents.
         * For example, if 40 documents are English, 30 documents German and 30 French,
         * all 100 documents will be clustered with English settings. In case of ties,
         * an arbitrary major language will be chosen. When the majority of documents
         * have undefined language, {@link MultilingualClustering#defaultLanguage}
         * will be used.
         */
        CLUSTER_IN_MAJORITY_LANGUAGE("Cluster all documents assuming the language of the majority");
        
        private String label;
        
        private LanguageAggregationStrategy(String label)
        {
            this.label = label;
        }

        @Override
        public String toString()
        {
            return label;
        }
    }

    /**
     * Logger for this class.
     */
    private final static Logger logger = LoggerFactory.getLogger(MultilingualClustering.class);

    /**
     * Language aggregation strategy. Determines how clusters generated for individual
     * languages should be combined to form the final result. Please see
     * {@link org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy} 
     * for the list of available options.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @Group(MULTILINGUAL_CLUSTERING)
    @Level(AttributeLevel.MEDIUM)
    public LanguageAggregationStrategy languageAggregationStrategy = LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE;

    /**
     * Default clustering language. The default language to use for documents with
     * undefined {@link org.carrot2.core.Document#LANGUAGE}.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @Group(MULTILINGUAL_CLUSTERING)
    @Level(AttributeLevel.MEDIUM)
    public LanguageCode defaultLanguage = LanguageCode.ENGLISH;

    /**
     * Document languages. The number of documents in each language. Empty string key means
     * unknown language.
     */
    @Output
    @Processing
    @Attribute
    @Group(MULTILINGUAL_CLUSTERING)
    @Level(AttributeLevel.MEDIUM)
    public Map<String, Integer> languageCounts; 
    
    /**
     * Majority language.
     * If {@link #languageAggregationStrategy} is 
     * {@link org.carrot2.text.clustering.MultilingualClustering.LanguageAggregationStrategy#CLUSTER_IN_MAJORITY_LANGUAGE},
     * this attribute will provide the majority language that was used to cluster all the documents.
     * If the majority of the documents have undefined language, this attribute will be 
     * empty and the clustering will be performed in the {@link #defaultLanguage}.
     */
    @Output
    @Processing
    @Attribute
    @Group(MULTILINGUAL_CLUSTERING)
    @Level(AttributeLevel.MEDIUM)
    public String majorityLanguage = ""; 
    
    public List<Cluster> process(List<Document> documents, IMonolingualClusteringAlgorithm algorithm)
    {
        languageCounts = Maps.newHashMap();
        
        if (documents.isEmpty())
        {
            return Lists.newArrayList();
        }
        
        if (LanguageAggregationStrategy.CLUSTER_IN_MAJORITY_LANGUAGE.equals(languageAggregationStrategy)) 
        {
            return clusterInMajorityLanguage(documents, algorithm);
        }

        // Clusters documents in each language separately,
        // creates a map of top-level Cluster instances named after the language code.
        final Map<LanguageCode, Cluster> clustersByLanguage = clusterByLanguage(documents, algorithm);
        final List<Cluster> clusters = Lists.newArrayList(clustersByLanguage.values());

        // For FLATTEN_ALL we combine all clusters
        if (clustersByLanguage.size() == 1 ||
            LanguageAggregationStrategy.FLATTEN_ALL.equals(languageAggregationStrategy))
        {
            // For FLATTEN_ALL, we simply mix up all clusters, moving all unclustered
            // documents under one common Other Topics cluster.
            final List<Cluster> flattenedClusters = Lists.newArrayList();
            for (Cluster cluster : clusters)
            {
                final List<Cluster> subclusters = cluster.getSubclusters();
                for (Cluster subcluster : subclusters)
                {
                    if (!subcluster.isOtherTopics())
                    {
                        flattenedClusters.add(subcluster);
                    }
                }
            }

            // If there's more than one language, sort clusters by their number of
            // documents, irrespectively of their original score. We don't know how
            // to normalize the score between languages (independent clustering)
            // and larger clusters are typically more intuitive (better?) than smaller clusters.
            if (clustersByLanguage.size() > 1)
            {
                Collections.sort(flattenedClusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);    
            }

            Cluster.appendOtherTopics(documents, flattenedClusters);
            return flattenedClusters;
        }
        else
        {
            Collections.sort(clusters, Collections.reverseOrder(Cluster.BY_SIZE_COMPARATOR));

            if (LanguageAggregationStrategy.FLATTEN_MAJOR_LANGUAGE.equals(languageAggregationStrategy))
            {
                // For FLATTEN_MAJOR_LANGUAGE, we flatten the first biggest language
                // cluster that has some nontrivial subclusters (clusters in that
                // language). If there's no cluster with nontrivial subclusters,
                // we default to FLATTEN_NONE.
                final Iterator<Cluster> iterator = clusters.iterator();
                Cluster majorLanguageCluster = null;
                try
                {
                    majorLanguageCluster = Iterators.find(iterator,
                        new Predicate<Cluster>()
                        {
                            public boolean apply(Cluster cluster)
                            {
                                return !cluster.getSubclusters().isEmpty();
                            }
                        });
                }
                catch (NoSuchElementException ignored)
                {
                    // When element is not found, Google Collections throws this exception
                }

                if (majorLanguageCluster != null)
                {
                    iterator.remove();
                    final List<Cluster> flattenedClusters = Lists.newArrayList();
                    flattenedClusters.addAll(majorLanguageCluster.getSubclusters());

                    final Cluster otherLanguages = new Cluster("Other Languages");
                    otherLanguages.addSubclusters(clusters);
                    flattenedClusters.add(otherLanguages);
                    return flattenedClusters;
                }
                else
                {
                    return clusters;
                }
            }
            else
            {
                return clusters;
            }
        }
    }

    /**
     * Clusters documents in each language separately.
     */
    private Map<LanguageCode, Cluster> clusterByLanguage(List<Document> documents,
        IMonolingualClusteringAlgorithm algorithm)
    {
        // Partition by language first. As Multimaps.index() does not handle null
        // keys, we'd need to index by LanguageCode string and have a dedicated empty
        // string for the null language.
        final ImmutableListMultimap<String, Document> documentsByLanguage = 
            Multimaps.index(documents, new Function<Document, String>()
            {
                public String apply(Document document)
                {
                    final LanguageCode language = document.getLanguage();
                    return language != null ? language.name() : "";
                }
            });

        // For each language, perform clustering. Please note that implementations of 
        // IMonolingualClusteringAlgorithm.cluster() are not guaranteed to be thread-safe
        // and hence the method must NOT be called concurrently.
        final Map<LanguageCode, Cluster> clusters = Maps.newHashMap();
        for (String language : documentsByLanguage.keySet())
        {
            final ImmutableList<Document> languageDocuments = documentsByLanguage.get(language);
            final LanguageCode languageCode = language.equals("") ? null : LanguageCode.valueOf(language);
            final Cluster languageCluster = new Cluster(
                languageCode != null ? languageCode.toString() : "Unknown Language");
            
            languageCounts.put(languageCode != null ? languageCode.getIsoCode() : "",
                languageDocuments.size());

            // Perform clustering
            final LanguageCode currentLanguage = languageCode != null ? languageCode : defaultLanguage;
            logger.debug("Performing monolingual clustering in: " + currentLanguage);
            final List<Cluster> clustersForLanguage = algorithm.process(
                languageDocuments, currentLanguage);

            if (clustersForLanguage.size() == 0 || 
                clustersForLanguage.size() == 1 && clustersForLanguage.get(0).isOtherTopics())
            {
                languageCluster.addDocuments(languageDocuments);
            }
            else
            {
                languageCluster.addSubclusters(clustersForLanguage);
            }

            clusters.put(languageCode, languageCluster);
        }

        return clusters;
    }
    
    private List<Cluster> clusterInMajorityLanguage(List<Document> documents,
        IMonolingualClusteringAlgorithm algorithm)
    {
        final Multiset<LanguageCode> counts = HashMultiset.create();
        for (Document d : documents)
        {
            counts.add(d.getLanguage());
        }
        LanguageCode majorityLanguage = defaultLanguage;
        int maxCount = 0;
        for (Entry<LanguageCode> entry : counts.entrySet())
        {
            if (entry.getElement() != null)
            {
                if (entry.getCount() > maxCount)
                {
                    maxCount = entry.getCount();
                    majorityLanguage = entry.getElement();
                    this.majorityLanguage = entry.getElement().getIsoCode();
                }
            } 
            languageCounts.put(entry.getElement() != null ? entry.getElement().getIsoCode() : "", 
                entry.getCount());
        }
        
        logger.debug("Performing clustering in majority language: " + majorityLanguage);
        final List<Cluster> clusters = algorithm.process(documents, majorityLanguage);
        Cluster.appendOtherTopics(documents, clusters);
        return clusters;
    }
}
