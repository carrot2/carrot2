
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

package org.carrot2.source.opensearch;

import org.carrot2.core.ComponentInitializationException;
import org.carrot2.core.DummyControllerContext;
import org.junit.Test;

/**
 * Basic test cases for {@link OpenSearchDocumentSource}.
 */
public class OpenSearchDocumentSourceTest
{
    @Test(expected = ComponentInitializationException.class)
    public void testSearchTermsNotPresent()
    {
        testFeedTemplate("http://test.com?sp=${startPage}");
    }

    @Test(expected = ComponentInitializationException.class)
    public void testNoStartPresent()
    {
        testFeedTemplate("http://test.com?q=${searchTerms}");
    }

    @Test(expected = ComponentInitializationException.class)
    public void testBothStartsPresent()
    {
        testFeedTemplate("http://test.com?sp=${startPage}&si=${startIndex}");
    }

    @Test(expected = ComponentInitializationException.class)
    public void testResultsPerPageNotSet()
    {
        testFeedTemplate("http://test.com?sp=${startPage}&q=${searchTerms}&c=${count}");
    }

    @Test
    public void testCorrectConfiguration()
    {
        final OpenSearchDocumentSource source = new OpenSearchDocumentSource();

        source.feedUrlTemplate = "http://test.com?sp=${startPage}&q=${searchTerms}&c=${count}";
        source.resultsPerPage = 20;

        final DummyControllerContext ctx = new DummyControllerContext();
        source.init(ctx);
        ctx.dispose();
    }

    private void testFeedTemplate(String template)
    {
        final OpenSearchDocumentSource source = new OpenSearchDocumentSource();
        source.feedUrlTemplate = template;

        final DummyControllerContext ctx = new DummyControllerContext();
        source.init(ctx);
        ctx.dispose();
    }
}
