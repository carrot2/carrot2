package org.carrot2.source.microsoft;

import org.carrot2.core.test.MultipartDocumentSourceTestBase;
import org.carrot2.source.SearchEngineMetadata;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Microsoft Live! document source.
 */
@RunWith(AnnotationRunner.class)
public class MicrosoftLiveDocumentSourceTest extends
    MultipartDocumentSourceTestBase<MicrosoftLiveDocumentSource>
{
    @Override
    public Class<MicrosoftLiveDocumentSource> getComponentClass()
    {
        return MicrosoftLiveDocumentSource.class;
    }

    @Override
    protected SearchEngineMetadata getSearchEngineMetadata()
    {
        return MicrosoftLiveDocumentSource.metadata;
    }
}
