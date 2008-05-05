package org.carrot2.source.google;

import java.util.concurrent.ExecutionException;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Microsoft Live! document source.
 */
@RunWith(AnnotationRunner.class)
public class GoogleDocumentSourceTest extends
    QueryableDocumentSourceTestBase<GoogleDocumentSource>
{
    @Override
    public Class<GoogleDocumentSource> getComponentClass()
    {
        return GoogleDocumentSource.class;
    }

    @Override
    public void testInCachingController() throws InterruptedException, ExecutionException
    {
        // Ignore this test because it requires > 50 results and Google returns only 32.
    }
}
