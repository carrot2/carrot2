
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
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.*;

/**
 * News search specific document source.
 * 
 * @see Bing3DocumentSource
 */
@Bindable(prefix = "Bing3NewsDocumentSource")
public class Bing3NewsDocumentSource extends Bing3DocumentSource
{
    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata metadata = 
        new MultipageSearchEngineMetadata(15, 100);

    /**
     * Specifies the sort order of results.
     */
    @Processing
    @Input
    @Attribute
    @Label("Sort order")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.FILTERING)    
    public SortOrder sortOrder;

    /**
     * Specifies news category.
     */
    @Processing
    @Input
    @Attribute
    @Label("Category")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.FILTERING)    
    public NewsCategory newsCategory;

    /**
     * Initialize source type properly.
     */
    public Bing3NewsDocumentSource()
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

        addIfNotEmpty(params, "NewsSortBy", stringValue(sortOrder));

        if (newsCategory != null)
        {
            params.add(new BasicNameValuePair("NewsCategory", stringValue(newsCategory.catValue)));
        }
    }
}
