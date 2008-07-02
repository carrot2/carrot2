package org.carrot2.core.test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.StringUtils;
import org.junit.Test;
import org.junitext.Prerequisite;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Common tests for {@link DocumentSource}s that accept a string query.
 */
public abstract class QueryableDocumentSourceTestBase<T extends DocumentSource> extends
    DocumentSourceTestBase<T>
{
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNoResultsQuery() throws Exception
    {
        runAndCheckNoResultsQuery();
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testSmallQuery() throws Exception
    {
        checkMinimumResults("apache", 50, 35);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testLargeQuery() throws Exception
    {
        checkMinimumResults("data mining", 300, 150);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testResultsTotal() throws Exception
    {
        runQuery("apache", 250);

        assertNotNull(processingAttributes.get(AttributeNames.RESULTS_TOTAL));
        assertTrue((Long) processingAttributes.get(AttributeNames.RESULTS_TOTAL) > 0);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void testURLsUnique() throws Exception
    {
        runQuery("apache", 200);

        assertFieldUnique((Collection<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS), Document.CONTENT_URL);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void testInCachingController() throws InterruptedException, ExecutionException
    {
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.QUERY, "test");
        attributes.put(AttributeNames.RESULTS, 50);

        // Cache results from all DataSources
        final CachingController cachingController = new CachingController(
            DocumentSource.class);
        cachingController.init(new HashMap<String, Object>());

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
                    return cachingController
                        .process(localAttributes, getComponentClass());
                }
            });
        }

        List<Future<ProcessingResult>> results = executorService.invokeAll(callables);

        List<Document> documents = null;
        int index = 0;
        for (Future<ProcessingResult> future : results)
        {
            ProcessingResult processingResult = future.get();
            final List<Document> documentsLocal = (List<Document>) processingResult
                .getAttributes().get(AttributeNames.DOCUMENTS);
            assertThat(documentsLocal).as("documents at " + index).isNotNull();
            assertThat(documentsLocal.size()).as("documents.size() at " + index)
                .isLessThanOrEqualTo(50).isGreaterThanOrEqualTo(35);

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
    }

    private void checkMinimumResults(String query, int resultsToRequest,
        int minimumExpectedResults)
    {
        int actualResults = runQuery("data mining", resultsToRequest);
        assertThat(actualResults).isGreaterThanOrEqualTo(minimumExpectedResults);
    }
    
    @SuppressWarnings("unchecked")
    protected void runAndCheckNoResultsQuery()
    {
        final int results = runQuery(ExternalApiTestBase.NO_RESULTS_QUERY, 100);
        if (results != 0)
        {
            final List<Document> documents = (List<Document>) processingAttributes
                .get(AttributeNames.DOCUMENTS);
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
