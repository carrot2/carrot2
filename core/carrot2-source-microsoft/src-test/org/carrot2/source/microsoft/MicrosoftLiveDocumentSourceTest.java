
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
 * Tests Microsoft Live! document source.
 */
public class MicrosoftLiveDocumentSourceTest extends
    MultipageDocumentSourceTestBase<MicrosoftLiveDocumentSource>
{
    @Override
    public Class<MicrosoftLiveDocumentSource> getComponentClass()
    {
        return MicrosoftLiveDocumentSource.class;
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
    
    protected double slack()
    {
        return 1.4;
    }
}
