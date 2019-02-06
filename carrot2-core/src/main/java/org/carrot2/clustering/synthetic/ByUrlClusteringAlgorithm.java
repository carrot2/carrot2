
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.synthetic;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Output;

/**
 * Hierarchically clusters documents according to their content URLs.
 * {@link Document#CONTENT_URL} property will be used to obtain a document's URL.
 * <p>
 * Groups at the top level of the hierarchy will correspond to the last segments of the
 * URLs, usually domain suffixes, such as ".com" or ".co.uk". Subgroups will be created
 * based on further segments of the URLs, very often domains subdomains, e.g. "yahoo.com",
 * "bbc.co.uk" and then e.g. "mail.yahoo.com", "news.yahoo.com". The "www" segment of the
 * URLs will be ignored.
 * <p>
 * Clusters will be ordered by size (number of documents) descendingly; in case of equal
 * sizes, alphabetically by URL, see {@link Cluster#BY_REVERSED_SIZE_AND_LABEL_COMPARATOR}.
 */
@Bindable(inherit = CommonAttributes.class)
@Label("By URL Clustering")
public class ByUrlClusteringAlgorithm extends ProcessingComponentBase implements
    IClusteringAlgorithm
{
    /** A set of URL segments to be ignored. */
    private static final Set<String> STOP_URL_PARTS;
    static
    {
        STOP_URL_PARTS = new HashSet<>();
        STOP_URL_PARTS.add("www");
    }

    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    /**
     * Clusters created by the algorithm.
     */
    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters = null;

    /**
     * Performs by URL clustering.
     */
    @Override
    public void process() throws ProcessingException
    {
        // Just in case we get a linked list, create an array of documents
        final Document [] documentArray = this.documents
            .toArray(new Document [this.documents.size()]);

        // Prepare an array of url parts
        final String [][] urlParts = buildUrlParts(documentArray);

        // Recursively build the cluster structure
        final List<Integer> documentIndexes = new ArrayList<Integer>(documentArray.length);
        for (int i = 0; i < documentArray.length; i++)
        {
            documentIndexes.add(i);
        }
        this.clusters = createClusters(documentArray, documentIndexes, urlParts, 0, "");
        
        if (clusters.size() == 0) {
            Cluster.appendOtherTopics(documents, clusters, "Other Sites");
        }
    }

    /**
     * The actual, recursive, clustering routine.
     */
    private List<Cluster> createClusters(Document [] documents,
        Collection<Integer> documentIndexes, String [][] urlParts, int level,
        String labelSuffix)
    {

        final LinkedHashMap<String, List<Integer>> urlPartToDocumentIndex = new LinkedHashMap<>();
        for (final Integer documentIndex : documentIndexes)
        {
            final String [] urlPartsForDocument = urlParts[documentIndex.intValue()];
            if (urlPartsForDocument != null && urlPartsForDocument.length > level
                && !STOP_URL_PARTS.contains(urlPartsForDocument[level]))
            {
                urlPartToDocumentIndex.compute(
                    urlPartsForDocument[level],
                    (k, list) -> {
                        if (list == null) list = new ArrayList<>();
                        list.add(documentIndex);
                        return list;
                    });
            }
        }

        final Set<Integer> documentsInClusters = new LinkedHashSet<Integer>();
        final List<Cluster> clusters = new ArrayList<Cluster>();
        for (final String urlPart : urlPartToDocumentIndex.keySet())
        {
            final List<Integer> indexes = urlPartToDocumentIndex.get(urlPart);

            if (indexes.size() > 1)
            {
                final Cluster cluster = new Cluster();
                String clusterLabel = urlPart
                    + (labelSuffix.length() > 0 ? "." + labelSuffix : "");

                final List<Cluster> subclusters = createClusters(documents, indexes,
                    urlParts, level + 1, clusterLabel);
                if (subclusters.size() > 1)
                {
                    cluster.addSubclusters(subclusters);
                }
                else
                {
                    // only one subcluster -- move the label one level up
                    if (subclusters.size() == 1)
                    {
                        final Cluster subcluster = subclusters.get(0);
                        clusterLabel = subcluster.getPhrases().get(0);
                        cluster.addDocuments(subcluster.getDocuments());
                        cluster.addSubclusters(subcluster.getSubclusters());
                    }
                    else
                    {
                        for (final Integer documentIndex : indexes)
                        {
                            cluster.addDocuments(documents[documentIndex.intValue()]);
                        }
                    }
                }

                cluster.addPhrases(clusterLabel);
                clusters.add(cluster);
                documentsInClusters.addAll(indexes);
            }
        }

        if (documentsInClusters.isEmpty())
        {
            return new ArrayList<>();
        }

        // Sort clusters
        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);

        // Add junk clusters
        final ArrayList<Document> documentsInCluster = new ArrayList<>(documentIndexes.size());
        for (Integer documentIndex : documentIndexes)
        {
            documentsInCluster.add(documents[documentIndex]);
        }

        Cluster.appendOtherTopics(documentsInCluster, clusters, "Other Sites");

        return clusters;
    }

    /**
     * For each documents builds an array of parts of their corresponding URLs.
     */
    final String [][] buildUrlParts(final Document [] documents)
    {
        final String [][] urlParts = new String [documents.length] [];
        for (int i = 0; i < documents.length; i++)
        {
            final String url = documents[i].getField(Document.CONTENT_URL);
            if (url == null)
            {
                continue;
            }

            int colonSlashSlashIndex = url.indexOf("://");
            if (colonSlashSlashIndex < 0)
            {
                colonSlashSlashIndex = 0;
            }
            else if (colonSlashSlashIndex + 3 >= url.length())
            {
                continue;
            }
            else
            {
                colonSlashSlashIndex += 3;
            }

            int slashIndex = url.indexOf('/', colonSlashSlashIndex + 3);
            if (slashIndex < 0)
            {
                slashIndex = url.length();
            }

            final String urlMainPart = url.substring(colonSlashSlashIndex, slashIndex)
                .toLowerCase();

            final String [] splitUrl = urlMainPart.split("\\.");
            ArrayUtils.reverse(splitUrl);
            urlParts[i] = splitUrl;
        }

        return urlParts;
    }
}
