package org.carrot2.source.microsoft;

import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Microsoft Live! document source.
 */
@RunWith(AnnotationRunner.class)
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
