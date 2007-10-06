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

package org.carrot2.core.impl;

import java.io.*;
import java.util.ArrayList;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.util.RawCluster2JSON;
import org.carrot2.core.util.RawDocument2JSON;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * This component acts as an interceptor in the processing chain and writes a JSON stream with the query, documents and
 * clusters.
 * 
 * @see SaveFilterComponentBase
 * @see SaveFilterComponentBase#PARAM_OUTPUT_FILE
 * @see SaveFilterComponentBase#PARAM_OUTPUT_STREAM
 */
public final class SaveJsonFilterComponent extends SaveFilterComponentBase
{
    /**
     * Save the XML with documents and/or clusters.
     */
    protected void endProcessing0(OutputStream outputStream, ArrayList rawDocuments, ArrayList rawClusters,
        boolean saveDocuments, boolean saveClusters) throws ProcessingException
    {
        try
        {
            final OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            final JSONWriter jsonWriter = new JSONWriter(writer);
            jsonWriter.object();

            if (saveDocuments)
            {
                final RawDocument [] docs = (RawDocument []) rawDocuments.toArray(new RawDocument [rawDocuments.size()]);
                jsonWriter.key("documents");
                jsonWriter.array();
                for (int i = 0; i < docs.length; i++)
                {
                    RawDocument2JSON.serialize(docs[i], jsonWriter);
                }
                jsonWriter.endArray();
            }

            if (saveClusters)
            {
                final RawCluster [] clusters = (RawCluster []) rawClusters.toArray(new RawCluster [rawClusters.size()]);
                jsonWriter.key("clusters");
                jsonWriter.array();
                for (int i = 0; i < clusters.length; i++)
                {
                    RawCluster2JSON.serialize(clusters[i], jsonWriter);
                }
                jsonWriter.endArray();
            }

            jsonWriter.endObject();
            writer.flush();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ProcessingException("UTF-8 encoding must be supported.", e);
        }
        catch (IOException e)
        {
            throw new ProcessingException("Could not serialize data because of an I/O error.", e);
        }
        catch (JSONException e)
        {
            throw new ProcessingException("Could not serialize data to JSON.", e);
        }
    }
}