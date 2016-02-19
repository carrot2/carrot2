
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.carrot2.core.ProcessingException;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.Bindable;

/**
 * Image search document source.
 * 
 * @see Bing3DocumentSource
 */
@Bindable(prefix = "Bing3ImageDocumentSource")
public class Bing3ImageDocumentSource extends Bing3DocumentSource
{
    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata metadata = 
        new MultipageSearchEngineMetadata(50, 400);

    /**
     * Initialize source type properly.
     */
    public Bing3ImageDocumentSource()
    {
        super(SourceType.IMAGE);
    }

    /**
     * Process the query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }
    
    @Override
    protected void appendSourceParams(ArrayList<NameValuePair> params)
    {
        super.appendSourceParams(params);
    }
}
