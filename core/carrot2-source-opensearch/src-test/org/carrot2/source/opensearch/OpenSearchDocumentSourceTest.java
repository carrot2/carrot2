package org.carrot2.source.opensearch;

import org.carrot2.core.ComponentInitializationException;
import org.junit.Test;

/**
 * Basic test cases for {@link OpenSearchDocumentSource}.
 */
public class OpenSearchDocumentSourceTest
{
    @Test(expected = ComponentInitializationException.class)
    public void testSearchTermsNotPresent()
    {
        OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = "http://test.com?sp=${startPage}";
        source.init();
    }

    @Test(expected = ComponentInitializationException.class)
    public void testNoStartPresent()
    {
        OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = "http://test.com?q=${searchTerms}";
        source.init();
    }

    @Test(expected = ComponentInitializationException.class)
    public void testBothStartsPresent()
    {
        OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = "http://test.com?sp=${startPage}&si=${startIndex}";
        source.init();
    }

    @Test(expected = ComponentInitializationException.class)
    public void testResultsPerPageNotSet()
    {
        OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = "http://test.com?sp=${startPage}&q=${searchTerms}&c=${count}";
        source.init();
    }

    @Test
    public void testCorrectConfiguration()
    {
        OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = "http://test.com?sp=${startPage}&q=${searchTerms}&c=${count}";
        source.resultsPerPage = 20;
        source.init();
    }
}
