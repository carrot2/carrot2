
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

package org.carrot2.source.pubmed;

import java.util.List;

import org.xml.sax.SAXException;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A SAX content handler that collects PubMed IDs.
 */
class PubMedIdSearchHandler extends PathTrackingHandler
{
    /** Collects IDs of PubMed entries to retrieve */
    private List<String> pubMedPrimaryIds;

    /** Total match count. */
    private long matchCount;

    public PubMedIdSearchHandler()
    {
        super.addTrigger("/eSearchResult/Count", new Trigger() {
            @Override
            public void afterElement(String localName, String path, String text)
            {
                matchCount = Long.parseLong(text);
            }
        });
        
        super.addTrigger("/eSearchResult/IdList/Id", new Trigger() {
            @Override
            public void afterElement(String localName, String path, String text)
            {
                pubMedPrimaryIds.add(text);
            }
        });
    }
    
    @Override
    public void startDocument() throws SAXException
    {
        matchCount = -1;
        pubMedPrimaryIds = Lists.newArrayList();
    }
    
    public List<String> getPubMedPrimaryIds()
    {
        return pubMedPrimaryIds;
    }

    public long getMatchCount()
    {
        return matchCount;
    }
}
