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
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.StringUtils;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Common tests for {@link IDocumentSource}s that accept a string query.
 */
public abstract class QueryableDocumentSourceTestBase<T extends IDocumentSource> extends
    DocumentSourceTestBase<T>
{
    @Test
    public void testNoResultsQuery() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        runAndCheckNoResultsQuery();
    }

    @Test
    public void testSmallQuery() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        runAndCheckMinimumResults(getSmallQueryText(), getSmallQuerySize(),
            getSmallQuerySize() / 2);
    }

    @Test
    public void testUtfCharacters() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        assumeTrue(hasUtfResults());
        runAndCheckMinimumResults("kaczyński", getSmallQuerySize(),
            getSmallQuerySize() / 2);
    }

    @Test
    public void testLargeQuery() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        runAndCheckMinimumResults(getLargeQueryText(), getLargeQuerySize(),
            getLargeQuerySize() / 2);
    }

    @Test
    public void testResultsTotal() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        assumeTrue(hasTotalResultsEstimate());
        runQuery(getSmallQueryText(), getSmallQuerySize());

        assertNotNull(resultAttributes.get(AttributeNames.RESULTS_TOTAL));
        assertTrue((Long) resultAttributes.get(AttributeNames.RESULTS_TOTAL) > 0);
    }

    @Test
    public void testURLsUnique() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        assumeTrue(mustReturnUniqueUrls());
        runQuery(getLargeQueryText(), getLargeQuerySize());
        assertFieldUnique(getDocuments(), Document.CONTENT_URL);
    }

    @Test
    public void testHtmlUnescaping()
    {
        assumeTrue(externalApiTestsEnabled());
        assumeTrue(canReturnEscapedHtml());
        runQuery("test", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        int i = 0;
        for (Document document : documents)
        {
            assertThat(document).as("doc[" + i++ + "]").stringFieldsDoNotMatchPattern(
                ".*&lt;.*");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInCachingController() throws InterruptedException, ExecutionException
    {
        assumeTrue(externalApiTestsEnabled());

        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.QUERY, getSmallQueryText());
        attributes.put(AttributeNames.RESULTS, getSmallQuerySize());

        // Cache results from all DataSources
        final Controller controller = getCachingController(initAttributes,
            IDocumentSource.class);

        int count = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        List<Callable<ProcessingResult>> callables = Lists.newArrayList();
        for (int i = 0; i < count; i++)
        {
            callables.add(new Callable<ProcessingResult>()
            {
                public ProcessingResult call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap(attributes);
                    return controller.process(localAttributes, getComponentClass());
                }
            });
        }

        final List<Future<ProcessingResult>> results = executorService
            .invokeAll(callables);

        List<Document> documents = null;
        int index = 0;
        for (Future<ProcessingResult> future : results)
        {
            ProcessingResult processingResult = future.get();
            final List<Document> documentsLocal = (List<Document>) processingResult
                .getAttributes().get(AttributeNames.DOCUMENTS);
            assertThat(documentsLocal).as("documents at " + index).isNotNull();
            assertThat(documentsLocal.size()).as("documents.size() at " + index)
                .isLessThanOrEqualTo(getSmallQuerySize()).isGreaterThanOrEqualTo(
                    getSmallQuerySize() / 2);

            // Should have same documents (from the cache)
            if (documents != null)
            {
                for (int i = 0; i < documents.size(); i++)
                {
                    assertSame(documents.get(i), documentsLocal.get(i));
                }
            }
            documents = documentsLocal;
            index++;
        }

        controller.dispose();
    }

    /**
     * Override to switch on checking non-English results.
     */
    protected boolean hasUtfResults()
    {
        return false;
    }

    /**
     * Override to customize small query size.
     */
    protected int getSmallQuerySize()
    {
        return 50;
    }

    /**
     * Override to customize small query text.
     */
    protected String getSmallQueryText()
    {
        return "blog";
    }

    /**
     * Override to customize large query size.
     */
    protected int getLargeQuerySize()
    {
        return 300;
    }

    /**
     * Override to customize large query text.
     */
    protected String getLargeQueryText()
    {
        return "test";
    }

    /**
     * Override to switch checking of total results estimates.
     */
    protected boolean hasTotalResultsEstimate()
    {
        return true;
    }

    /**
     * Override to switch checking of HTML unescaping.
     */
    protected boolean canReturnEscapedHtml()
    {
        return true;
    }

    /**
     * Override to switch checking of URL uniqueness.
     */
    protected boolean mustReturnUniqueUrls()
    {
        return true;
    }

    /**
     * Override to customize no results query.
     */
    protected String getNoResultsQueryText()
    {
        return getNoResultsQuery();
    }

    /**
     * Override to customize no results query.
     */
    public static String getNoResultsQuery()
    {
        final int words = 5;
        final int chars = 8;
        final Random random = new Random();

        final StringBuilder query = new StringBuilder();
        for (int i = 0; i < words; i++)
        {
            for (int j = 0; j < chars; j++)
            {
                query.append((char) ('a' + random.nextInt('z' - 'a')));
            }
            query.append(random.nextInt(1000000));
            query.append(' ');
        }

        return query.toString();
    }

    protected void runAndCheckMinimumResults(String query, int resultsToRequest,
        int minimumExpectedResults)
    {
        int actualResults = runQuery(query, resultsToRequest);
        assertThat(actualResults).isGreaterThanOrEqualTo(minimumExpectedResults);
    }

    protected void runAndCheckNoResultsQuery()
    {
        runAndCheckNoResultsQuery(getSmallQuerySize());
    }

    protected void runAndCheckNoResultsQuery(int size)
    {
        final int results = runQuery(getNoResultsQueryText(), size);
        if (results != 0)
        {
            final List<Document> documents = getDocuments();
            final String urls = StringUtils.toString(Lists.transform(documents,
                new Function<Document, String>()
                {
                    public String apply(Document document)
                    {
                        return document.getField(Document.CONTENT_URL);
                    }
                }), ", ");
            fail("Expected 0 results but found: " + results + " (urls: " + urls + ")");
        }
    }
}
