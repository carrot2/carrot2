
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
import java.io.StringWriter;

import junit.framework.TestCase;

import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentBase;
import org.json.*;

/**
 * Converts {@link RawDocument} to JSON format.
 * 
 * @see <a href="http://www.json.org">JSON</a>
 * @author Dawid Weiss
 */
public final class RawDocument2JSONTest extends TestCase
{
    public RawDocument2JSONTest(String test)
    {
        super(test);
    }

    public void testRawDocumentSerialization() throws IOException, JSONException
    {
        final RawDocument [] docs = new RawDocument []
        {
            // Simple document.
            new RawDocumentBase("http://nourl.org/1", "title1", "snippet1")
            {
                public Object getId()
                {
                    return new Integer(1);
                }
            },
            // Some funky characters.
            new RawDocumentBase("http://nourl.org/2", "\"\'\"łóńżźć", /* no snippet. */null)
            {
                {
                    this.setProperty(RawDocument.PROPERTY_SOURCES, new String []
                    {
                        "aa", "bb"
                    });
                }

                public Object getId()
                {
                    return new Integer(2);
                }
            }
        };

        final StringWriter writer = new StringWriter();
        RawDocument2JSON.serialize(docs, writer);

        final JSONArray array = new JSONObject(writer.toString()).getJSONArray("documents");
        assertEquals(2, array.length());

        for (int i = 0; i < docs.length; i++)
        {
            final JSONObject object = array.getJSONObject(i);
            final RawDocument rd = docs[i];

            assertEquals(object.getString("id"), rd.getId().toString());

            check(RawDocument.PROPERTY_URL, object, rd);
            check(RawDocument.PROPERTY_TITLE, object, rd);
            check(RawDocument.PROPERTY_SNIPPET, object, rd);
            check(RawDocument.PROPERTY_SOURCES, object, rd);
        }

        // Check the special property.
        assertTrue(array.getJSONObject(1).has(RawDocument.PROPERTY_SOURCES));
    }

    private void check(String propName, JSONObject object, RawDocument rd) throws JSONException
    {
        if (rd.getProperty(propName) == null)
        {
            assertTrue(!object.has(propName));
        }
        else
        {
            if (propName.equals(RawDocument.PROPERTY_SOURCES))
            {
                final JSONArray jsonArray = object.getJSONArray(RawDocument.PROPERTY_SOURCES);
                final String [] sources = (String []) rd.getProperty(RawDocument.PROPERTY_SOURCES);
                for (int i = 0; i < sources.length; i++) {
                    assertEquals(sources[i], jsonArray.get(i));
                }
            }
            else
            {
                assertEquals(object.getString(propName), rd.getProperty(propName));
            }
        }
    }
}
