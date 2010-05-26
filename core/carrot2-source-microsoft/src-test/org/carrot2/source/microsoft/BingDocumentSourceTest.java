
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft;

import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;

/**
 * Tests Microsoft Bing document source.
 */
public class BingDocumentSourceTest extends
    MultipageDocumentSourceTestBase<BingDocumentSource>
{
    @Override
    public Class<BingDocumentSource> getComponentClass()
    {
        return BingDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return MicrosoftLiveDocumentSource.metadata;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
