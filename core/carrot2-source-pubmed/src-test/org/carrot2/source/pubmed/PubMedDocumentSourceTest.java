
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

import org.carrot2.core.test.QueryableDocumentSourceTestBase;

/**
 * Test cases for {@link PubMedDocumentSource}.
 */
public class PubMedDocumentSourceTest extends
    QueryableDocumentSourceTestBase<PubMedDocumentSource>
{
    @Override
    public Class<PubMedDocumentSource> getComponentClass()
    {
        return PubMedDocumentSource.class;
    }

    @Override
    protected boolean canReturnEscapedHtml()
    {
        return false;
    }

    @Override
    protected String getLargeQueryText()
    {
        return "blood";
    }

    @Override
    protected String getSmallQueryText()
    {
        return "heart";
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return false;
    }

    @Override
    protected String getNoResultsQueryText()
    {
        return "chrzęszczyrzeboszyce";
    }

    @Override
    protected boolean mustReturnUniqueUrls()
    {
        return false;
    }
    
    protected int getLargeQuerySize()
    {
        return 150;
    }
}
