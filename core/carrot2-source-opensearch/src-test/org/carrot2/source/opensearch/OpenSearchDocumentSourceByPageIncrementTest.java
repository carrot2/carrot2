package org.carrot2.source.opensearch;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link OpenSearchDocumentSource} with feeds where start result index is
 * specified.
 */
@RunWith(AnnotationRunner.class)
public class OpenSearchDocumentSourceByPageIncrementTest extends
    QueryableDocumentSourceTestBase<OpenSearchDocumentSource>
{
    @Override
    public Class<OpenSearchDocumentSource> getComponentClass()
    {
        return OpenSearchDocumentSource.class;
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 120;
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 30;
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    public void prepareComponent()
    {
        super.prepareComponent();

        final String base = OpenSearchDocumentSource.class.getName();
        initAttributes
            .put(base + ".feedUrlTemplate",
                "http://blogs.icerocket.com/search?q=${searchTerms}&rss=1&os=1&p=${startPage}&n=${count}&tab=blog");
        initAttributes.put(base + ".resultsPerPage", 50);
        initAttributes.put(base + ".maximumResults", 200);
    }
}
