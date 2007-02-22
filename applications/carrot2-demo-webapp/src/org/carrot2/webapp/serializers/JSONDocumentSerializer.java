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

import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.util.RawDocument2JSON;
import org.carrot2.webapp.Constants;
import org.carrot2.webapp.RawDocumentsSerializer;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * <p>
 * A document serializer which produces JSON output suitable for processing with automated tools (integration with other
 * languages).
 * <p>
 * The response contains an object with a property named <code>documents</code>. Value of this property is an array
 * of objects, each with the properties corresponding to constants defined in {@link RawDocument} interface. For
 * example, property <code>snippet</code> contains a string with the snippet of a given document. If this property is
 * not present, the snippet was not available.
 * <p>
 * If a processing error occurs, the response object contains a property named <code>exception</code>, pointing to an
 * object with members: <code>class</code>, <code>message</code> and (if available) <code>nested-exception</code>.
 * 
 * @author Dawid Weiss
 */
final class JSONDocumentSerializer implements RawDocumentsSerializer
{
    private Writer writer;
    private ArrayList docs = new ArrayList(100);

    /**
     * 
     */
    public JSONDocumentSerializer()
    {
        // Do nothing.
    }

    /**
     * 
     */
    public String getContentType()
    {
        return Constants.MIME_JSON;
    }

    /**
     * 
     */
    public void startResult(OutputStream os, String query) throws IOException
    {
        this.writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        docs.clear();
    }

    /**
     * 
     */
    public void write(RawDocument doc) throws IOException
    {
        docs.add(doc);
    }

    /**
     * 
     */
    public void endResult() throws IOException
    {
        final RawDocument [] docsArray = (RawDocument []) docs.toArray(new RawDocument [docs.size()]);
        RawDocument2JSON.serialize(docsArray, writer);
        this.writer.flush();
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
            processingError(writer, cause);
            writer.endObject();
        }
        catch (JSONException e)
        {
            throw new IOException("Could not serialize processing error: " + e.toString());
        }
    }
    
    /**
     * 
     */
    final static void processingError(JSONWriter writer, Throwable cause) throws JSONException
    {
        writer.object();
        writer.key("class").value(cause.getClass().getName());
        if (cause.getMessage() != null)
        {
            writer.key("message").value(cause.getMessage());
        }
        if (cause.getCause() != null)
        {
            writer.key("nested-exception");
            processingError(writer, cause.getCause());
        }
        writer.endObject();
    }
}
