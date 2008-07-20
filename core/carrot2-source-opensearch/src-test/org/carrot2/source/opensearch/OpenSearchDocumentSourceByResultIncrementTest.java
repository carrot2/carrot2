package org.carrot2.source.opensearch;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;

/**
 * Test cases for {@link OpenSearchDocumentSource} with feeds where start result index is
 * specified.
 */
public class OpenSearchDocumentSourceByResultIncrementTest extends
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
                "http://www.indeed.com/opensearch?q=${searchTerms}&start=${startIndex}&limit=${count}");
        initAttributes.put(base + ".resultsPerPage", 50);
        initAttributes.put(base + ".maximumResults", 200);
    }
}
