
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

package org.carrot2.core.test;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.carrot2.core.IDocumentSource;
import org.carrot2.source.*;
import org.carrot2.source.MultipageSearchEngine.SearchMode;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

/**
 * Common tests for {@link IDocumentSource}s that can make more than one search requests to
 * get results for one query.
 */
public abstract class MultipageDocumentSourceTestBase<T extends IDocumentSource> extends
    QueryableDocumentSourceTestBase<T>
{
    /**
     * Metadata for the {@link MultipageSearchEngine} being tested.
     */
    protected abstract MultipageSearchEngineMetadata getSearchEngineMetadata();

    @Test
    public void testQueryLargerThanPage() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());

        final int needed = getSearchEngineMetadata().resultsPerPage * 2
            + getSearchEngineMetadata().resultsPerPage / 2;

        // Allow some slack (duplicated URLs).
        final int documentsReturned = runQuery("test", needed);

        assertThat(documentsReturned).isGreaterThan((int) (needed / slack()));
    }

    /**
     * Some sources return fewer results than requested, slack aims to account for this.
     * Slack can be used in asserts on the number of returned documents like this:
     * 
     * <pre>assertThat(documentsReturned).isGreaterThan((int) (needed / slack()))</pre>
     * 
     * See {@link #testQueryLargerThanPage()}.
     */
    protected double slack()
    {
        return 1.25;
    }
    
    @Test
    public void testConservativeMode() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        processingAttributes.put("search-mode", SearchMode.CONSERVATIVE);

        runAndCheckNoResultsQuery();
        assertEquals(1, resultAttributes.get(AttributeUtils.getKey(
            SearchEngineStats.class, "pageRequests")));
    }

    @Test
    public void testSpeculativeMode() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        processingAttributes.put("search-mode", SearchMode.SPECULATIVE);

        runAndCheckNoResultsQuery(getSearchEngineMetadata().resultsPerPage + 1);
        assertEquals(2, resultAttributes.get(AttributeUtils.getKey(
            SearchEngineStats.class, "pageRequests")));
    }
}
