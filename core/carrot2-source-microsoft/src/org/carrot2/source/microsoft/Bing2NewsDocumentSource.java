
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.*;

/**
 * News search specific document source fetching from Bing using Bing2 API. 
 */
@Bindable(prefix = "Bing2NewsDocumentSource")
public class Bing2NewsDocumentSource extends Bing2DocumentSource
{
    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata metadata = 
        new MultipageSearchEngineMetadata(15, 100);

    /**
     * Specifies the sort order of results.
     * 
     * @label Sort order
     * @group Results filtering
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    public SortOrder sortOrder;

    /**
     * Initialize source type properly.
     */
    public Bing2NewsDocumentSource()
    {
        super(SourceType.NEWS);
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
        addIfNotEmpty(params, "News.SortBy", sortOrder);
    }
}
