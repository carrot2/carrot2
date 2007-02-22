package org.carrot2.core.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Converts {@link RawCluster}s to JSON format.
 * 
 * @see <a href="http://www.json.org">JSON</a>
 * @author Dawid Weiss
 */
public final class RawCluster2JSON
{
    /** No instances. Static methods only. */
    private RawCluster2JSON()
    {
        // no instances.
    }

    /**
     * Converts {@link RawCluster}s to JSON format.
     */
    public static void serialize(final RawCluster [] clusters, final Writer writer) throws IOException
    {
        try
        {
            final JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.object().key("clusters");
            jsonWriter.array();
            for (int i = 0; i < clusters.length; i++)
            {
                serialize(clusters[i], jsonWriter);
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        }
        catch (JSONException e)
        {
            throw new IOException("Could not serialize clusters: " + e.toString());
        }
    }

    /**
     * Adds a single {@link RawCluster} to a {@link JSONWriter}.
     */
    private static void serialize(final RawCluster cluster, final JSONWriter jsonWriter) throws JSONException
    {
        jsonWriter.object();

        // Description phrases
        jsonWriter.key("description");
        jsonWriter.array();
        for (final Iterator i = cluster.getClusterDescription().iterator(); i.hasNext();)
        {
            final String phrase = (String) i.next();
            jsonWriter.value(phrase);
        }
        jsonWriter.endArray();

        // Cluster properties
        if (cluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null) {
            jsonWriter.key("junk").value(true);
        }
        
        if (cluster.getProperty(RawCluster.PROPERTY_SCORE) != null) {
            jsonWriter.key("score").value(((Double) cluster.getProperty(RawCluster.PROPERTY_SCORE)).doubleValue());
        }

        // Documents.
        jsonWriter.key("documents");
        jsonWriter.array();
        for (final Iterator i = cluster.getDocuments().iterator(); i.hasNext();)
        {
            final RawDocument document = (RawDocument) i.next();
            jsonWriter.value(document.getId().toString());
        }
        jsonWriter.endArray();

        // Sub clusters.
        jsonWriter.key("clusters");
        jsonWriter.array();
        for (final Iterator i = cluster.getSubclusters().iterator(); i.hasNext();)
        {
            final RawCluster subcluster = (RawCluster) i.next();
            serialize(subcluster, jsonWriter);
        }
        jsonWriter.endArray();

        jsonWriter.endObject();
    }
}
