
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.opensearch;

import org.carrot2.core.DummyControllerContext;
import org.carrot2.core.ProcessingException;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Basic test cases for {@link OpenSearchDocumentSource}.
 */
public class OpenSearchDocumentSourceTest extends CarrotTestCase
{
    @Test(expected = ProcessingException.class)
    public void testSearchTermsNotPresent()
    {
        testFeedTemplate("http://test.com?sp=${startPage}");
    }

    @Test(expected = ProcessingException.class)
    public void testNoStartPresent()
    {
        testFeedTemplate("http://test.com?q=${searchTerms}");
    }

    @Test(expected = ProcessingException.class)
    public void testBothStartsPresent()
    {
        testFeedTemplate("http://test.com?sp=${startPage}&si=${startIndex}");
    }

    @Test(expected = ProcessingException.class)
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
        source.resultsPerPage = 0;
        source.feedUrlTemplate = template;
        source.beforeProcessing();
    }
}
