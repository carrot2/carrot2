package org.carrot2.source.boss;

import org.carrot2.core.DocumentSource;
import org.carrot2.core.test.MultipartDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Yahoo Boss {@link DocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class BossDocumentSourceTest extends
    MultipartDocumentSourceTestBase<BossDocumentSource>
{
    @Override
    public Class<BossDocumentSource> getComponentClass()
    {
        return BossDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return BossSearchService.DEFAULT_METADATA;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
