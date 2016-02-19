
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

import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;

import org.carrot2.shaded.guava.common.base.Strings;

/**
 * Web search specific document source. 
 * 
 * @see Bing3DocumentSource
 */
@Bindable(prefix = "Bing3WebDocumentSource")
public class Bing3WebDocumentSource extends Bing3DocumentSource
{
    /** Web search specific metadata. */
    final static MultipageSearchEngineMetadata metadata = 
        new MultipageSearchEngineMetadata(50, 950);

    /**
     * Site restriction to return results under a given URL. Example:
     * <tt>http://www.wikipedia.org</tt> or simply <tt>wikipedia.org</tt>.
     */
    @Processing
    @Input
    @Attribute
    @Label("Site restriction")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.FILTERING)        
    public String site;

    /**
     * Initialize source type properly.
     */
    public Bing3WebDocumentSource()
    {
        super(SourceType.WEB);
    }
    
    /**
     * Process the query.
     */
    @Override
    public void process() throws ProcessingException
    {
        if (!Strings.isNullOrEmpty(site))
        {
            query = Strings.nullToEmpty(query) + " site:" + site;
        }

        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }
}
