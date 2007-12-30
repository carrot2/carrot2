/**
 *
 */
package org.carrot2.examples;

import java.util.Collection;

import org.carrot2.core.*;

/**
 * A few utility methods required for API examples.
 */
public class ExampleUtils
{
    @SuppressWarnings("unchecked")
    public static void displayResults(ProcessingResult processingResult)
    {
        final Collection<Document> documents = processingResult.getDocuments();
        final Collection<Cluster> clusters = processingResult.getClusters();

        // Show documents
        System.out.println("Collected " + documents.size() + " documents\n");
        for (final Document document : documents)
        {
            displayDocument(0, document);
        }

        // Show clusters
        System.out.println("\n\nCreated " + clusters.size() + " clusters\n");
        int clusterNumber = 1;
        for (final Cluster cluster : clusters)
        {
            displayCluster(0, "" + clusterNumber++, cluster);
        }
    }

    private static void displayDocument(final int level, Document document)
    {
        final String indent = getIndent(level);

        System.out.printf(indent + "[%2d] ", document.getId());
        System.out.println(document.getField(Document.TITLE));
        System.out.println(indent + "     " + document.getField(Document.CONTENT_URL)
            + "]\n");
    }

    private static void displayCluster(final int level, String tag, Cluster cluster)
    {
        final String label = cluster.getLabel();

        // indent up to level and display this cluster's description phrase
        for (int i = 0; i < level; i++)
        {
            System.out.print("  ");
        }
        System.out.println(label + " (" + cluster.getDocuments().size() + " documents)");

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
