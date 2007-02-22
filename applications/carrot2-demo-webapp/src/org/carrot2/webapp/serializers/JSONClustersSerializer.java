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

package org.carrot2.webapp.serializers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.util.RawCluster2JSON;
import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawClustersSerializer;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * <p>
 * A consumer of {@link RawDocument}s which serializes them to JSON format suitable for further processing.
 * <p>
 * The response contains an object with a property named <code>clusters</code>. Value of this property is an array of
 * objects, each with the following properties:
 * <ul>
 * <li><code>description</code> - An array of strings with phrases describing this cluster,
 * <li><code>junk</code> - boolean <code>true</code> if this cluster is junk, not present otherwise,
 * <li><code>score</code> - a <code>double</code> value with the score of this cluster or not present,
 * <li><code>documents</code> - an array of strings with identifiers of clustered documents (may be empty, always
 * present),
 * <li><code>clusters</code> - an array of subclusters (may be empty, always present).
 * </ul>
 * 
 * @author Dawid Weiss
 */
public class JSONClustersSerializer implements RawClustersSerializer
{
    private final ArrayList clusters = new ArrayList(25);
    private Writer writer;

    /**
     * 
     */
    public final String getContentType()
    {
        return Constants.MIME_JSON;
    }

    /**
     * 
     */
    public final void startResult(OutputStream os, List rawDocumentsList, HttpServletRequest request, String query)
        throws IOException
    {
        this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
    }

    /**
     * 
     */
    public final void write(RawCluster cluster) throws IOException
    {
        clusters.add(cluster);
    }

    /**
     * 
     */
    public final void endResult() throws IOException
    {
        final RawCluster [] docsArray = (RawCluster []) clusters.toArray(new RawCluster [clusters.size()]);
        RawCluster2JSON.serialize(docsArray, writer);
        try
        {
            writer.flush();
        }
        catch (IOException e)
        {
            // ignore.
        }
        this.writer = null;
    }

    /**
     * 
     */
    public void processingError(Throwable cause) throws IOException
    {
        try
        {
            final JSONWriter writer = new JSONWriter(this.writer);
            writer.object().key("exception");
            JSONDocumentSerializer.processingError(writer, cause);
            writer.endObject();
        }
        catch (JSONException e)
        {
            throw new IOException("Could not serialize processing error: " + e.toString());
        }
    }
}
