/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.facet;

import java.util.*;

import org.apache.commons.collections.MapUtils;
import org.carrot2.core.clustering.*;
import org.carrot2.util.ArrayUtils;

/**
 * Divides input documents accodrding to the different domains and part of URL that point
 * to them. This class is thread-safe.
 * 
 * @author Stanislaw Osinski
 */
public class ByUrlFacetGenerator implements FacetGenerator
{
    public final static ByUrlFacetGenerator INSTANCE = new ByUrlFacetGenerator();

    private static final Set STOP_URL_PARTS;
    static
    {
        STOP_URL_PARTS = new HashSet();
        STOP_URL_PARTS.add("www");
    }

    public List generateFacets(List rawDocuments)
    {
        // Just in case we get a linked list, create an array of documents
        final RawDocument [] documents = (RawDocument []) rawDocuments
            .toArray(new RawDocument [rawDocuments.size()]);

        // Prepare an array of url parts
        String [][] urlParts = buildUrlParts(documents);

        // Recursively build the cluster structure
        List documentIndexes = new ArrayList(documents.length);
        for (int i = 0; i < rawDocuments.size(); i++)
        {
            documentIndexes.add(new Integer(i));
        }
        List clusters = createClusters(documents, documentIndexes, urlParts, 0, "");

        return clusters;
    }

    private List createClusters(RawDocument [] documents, List documentIndexes,
        String [][] urlParts, int level, String labelSuffix)
    {
        Map urlPartToDocumentIndex = MapUtils.multiValueMap(new HashMap());
        for (Iterator it = documentIndexes.iterator(); it.hasNext();)
        {
            Integer documentIndex = (Integer) it.next();
            String [] urlPartsForDocument = urlParts[documentIndex.intValue()];
            if (urlPartsForDocument != null && urlPartsForDocument.length > level
                && !STOP_URL_PARTS.contains(urlPartsForDocument[level]))
            {
                urlPartToDocumentIndex.put(urlPartsForDocument[level], documentIndex);
            }
        }

        Set documentsInClusters = new HashSet();
        List clusters = new ArrayList();
        for (Iterator it = urlPartToDocumentIndex.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            String urlPart = (String) entry.getKey();
            List indexes = (List) entry.getValue();

            if (indexes.size() > 1)
            {
                RawClusterBase cluster = new RawClusterBase();
                String clusterLabel = urlPart
                    + (labelSuffix.length() > 0 ? "." + labelSuffix : "");

                List subclusters = createClusters(documents, indexes, urlParts,
                    level + 1, clusterLabel);
                if (subclusters.size() > 1)
                {
                    cluster.addSubclusters(subclusters);
                }
                else
                {
                    // only one subcluster -- move the label one level up
                    if (subclusters.size() == 1)
                    {
                        final RawCluster subcluster = (RawCluster) subclusters.get(0);
                        clusterLabel = (String) (subcluster).getClusterDescription().get(
                            0);
                        cluster.addDocuments(subcluster.getDocuments());
                        cluster.addSubclusters(subcluster.getSubclusters());
                    }
                    else
                    {
                        for (Iterator it2 = indexes.iterator(); it2.hasNext();)
                        {
                            Integer documentIndex = (Integer) it2.next();
                            cluster.addDocument(documents[documentIndex.intValue()]);
                        }
                    }
                }

                cluster.addLabel(clusterLabel);
                clusters.add(cluster);
                documentsInClusters.addAll(indexes);
            }
        }

        // Sort clusters
        Collections.sort(clusters, RawClusterUtils.BY_SIZE_AND_NAME_COMPARATOR);

        if (documentsInClusters.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        else
        {
            RawClusterBase otherUrls = new RawClusterBase();
            otherUrls.addLabel("Other Sites");
            boolean junkClusterEmpty = true;
            for (Iterator it = documentIndexes.iterator(); it.hasNext();)
            {
                Integer documentIndex = (Integer) it.next();
                if (!documentsInClusters.contains(documentIndex))
                {
                    otherUrls.addDocument(documents[documentIndex.intValue()]);
                    junkClusterEmpty = false;
                }
            }

            if (!junkClusterEmpty)
            {
                clusters.add(otherUrls);
            }
        }

        return clusters;
    }

    String [][] buildUrlParts(final RawDocument [] documents)
    {
        final String [][] urlParts = new String [documents.length] [];
        for (int i = 0; i < documents.length; i++)
        {
            final String url = documents[i].getUrl();
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
