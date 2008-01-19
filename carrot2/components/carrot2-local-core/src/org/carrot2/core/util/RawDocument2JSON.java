
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.util;

import java.io.IOException;
import java.io.Writer;

import org.carrot2.core.clustering.RawDocument;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Converts {@link RawDocument} to JSON format.
 * 
 * @see <a href="http://www.json.org">JSON</a>
 * @author Dawid Weiss
 */
public final class RawDocument2JSON
{
    /**
     * Plain (easily convertable to a String) properties.
     */
    private final static String [] propertyNames = new String []
    {
        RawDocument.PROPERTY_URL, RawDocument.PROPERTY_TITLE, RawDocument.PROPERTY_SNIPPET,
        RawDocument.PROPERTY_LANGUAGE,
    };

    /** No instances. Static methods only. */
    private RawDocument2JSON()
    {
        // no instances.
    }

    /**
     * Converts standard properties of a {@link RawDocument} to JSON format.
     * 
     * @see RawDocument#PROPERTY_LANGUAGE
     * @see RawDocument#PROPERTY_SNIPPET
     * @see RawDocument#PROPERTY_SOURCES
     * @see RawDocument#PROPERTY_TITLE
     * @see RawDocument#PROPERTY_URL
     * @see RawDocument#getId()
     */
    public static void serialize(final RawDocument [] doc, final Writer writer) throws IOException
    {
        try
        {
            final JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.object().key("documents");
            jsonWriter.array();
            for (int i = 0; i < doc.length; i++)
            {
                serialize(doc[i], jsonWriter);
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        }
        catch (JSONException e)
        {
            throw new IOException("Could not serialize document: " + e.toString());
        }
    }

    /**
     * Adds a single {@link RawDocument} to a {@link JSONWriter}.
     */
    public static void serialize(final RawDocument document, final JSONWriter jsonWriter) throws JSONException
    {
        jsonWriter.object().key("id").value(document.getId());

        for (int i = 0; i < propertyNames.length; i++)
        {
            final String key = propertyNames[i];
            final Object value = document.getProperty(key);
            if (value != null)
            {
                jsonWriter.key(key).value(value);
            }
        }

        // Special properties now.
        final String [] sources = (String []) document.getProperty(RawDocument.PROPERTY_SOURCES);
        if (sources != null)
        {
            jsonWriter.key(RawDocument.PROPERTY_SOURCES);
            jsonWriter.array();
            for (int i = 0; i < sources.length; i++)
            {
                jsonWriter.value(sources[i]);
            }
            jsonWriter.endArray();
        }

        jsonWriter.endObject();
    }
}
