
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;

/**
 * Simple console formatter for dumping {@link ProcessingResult}. 
 */
public class ConsoleFormatter
{
    public static void displayResults(ProcessingResult processingResult)
    {
        final Collection<Document> documents = processingResult.getDocuments();
        final Collection<Cluster> clusters = processingResult.getClusters();
        final Map<String, Object> attributes = processingResult.getAttributes();

        // Show documents
        if (documents != null)
        {
            System.out.println("Collected " + documents.size() + " documents\n");
            for (final Document document : documents)
            {
                displayDocument(0, document);
            }
        }

        // Show clusters
        if (clusters != null)
        {
            System.out.println("\n\nCreated " + clusters.size() + " clusters\n");
            int clusterNumber = 1;
            for (final Cluster cluster : clusters)
            {
                displayCluster(0, "" + clusterNumber++, cluster);
            }
        }

        // Show attributes other attributes
        System.out.println("Attributes:");
        for (final Map.Entry<String, Object> attribute : attributes.entrySet())
        {
            if (!AttributeNames.DOCUMENTS.equals(attribute.getKey())
                && !AttributeNames.CLUSTERS.equals(attribute.getKey()))
            {
                System.out.println(attribute.getKey() + ":   " + attribute.getValue());
            }
        }
    }

    private static void displayDocument(final int level, Document document)
    {
        final String indent = getIndent(level);

        System.out.printf(indent + "[%2d] ", document.getId());
        System.out.println(document.getField(Document.TITLE));
        final String url = document.getField(Document.CONTENT_URL);
        if (StringUtils.isNotBlank(url))
        {
            System.out.println(indent + "     " + url);
        }
        System.out.println();
    }

    private static void displayCluster(final int level, String tag, Cluster cluster)
    {
        final String label = cluster.getLabel();

        // indent up to level and display this cluster's description phrase
        for (int i = 0; i < level; i++)
        {
            System.out.print("  ");
        }
        System.out.println(label + " (" + cluster.getAllDocuments().size() + " documents)");

        // if this cluster has documents, display three topmost documents.
        for (final Document document : cluster.getDocuments())
        {
            displayDocument(level + 1, document);
        }

        // finally, if this cluster has subclusters, descend into recursion.
        final int num = 1;
        for (final Cluster subcluster : cluster.getSubclusters())
        {
            displayCluster(level + 1, tag + "." + num, subcluster);
        }
    }

    private static String getIndent(final int level)
    {
        final StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++)
        {
            indent.append("  ");
        }

        return indent.toString();
    }
}
