package org.carrot2.source.microsoft;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

import com.google.common.collect.*;

/**
 * Tests Microsoft Live! document source.
 */
@RunWith(AnnotationRunner.class)
public class MicrosoftLiveDocumentSourceTest extends
    DocumentSourceTestBase<MicrosoftLiveDocumentSource>
{
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNoResultsQuery() throws Exception
    {
        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testResultsTotal() throws Exception
    {
        runQuery("apache", 50);

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
                    return cachingController.process(localAttributes,
                        MicrosoftLiveDocumentSource.class);
                }
            });
        }

        List<Future<ProcessingResult>> results = executorService.invokeAll(callables);

        List<Document> documents = null;
        for (Future<ProcessingResult> future : results)
        {
            ProcessingResult processingResult = future.get();
            final List<Document> documentsLocal = (List<Document>) processingResult
                .getAttributes().get(AttributeNames.DOCUMENTS);
            assertNotNull(documentsLocal);
            assertTrue(documentsLocal.size() <= 50 && documentsLocal.size() >= 35);

            // Should have same documents (from the cache)
            if (documents != null)
            {
                for (int i = 0; i < documents.size(); i++)
                {
                    assertSame(documents.get(i), documentsLocal.get(i));
                }
            }
            documents = documentsLocal;
        }
    }

    @Override
    public Class<MicrosoftLiveDocumentSource> getComponentClass()
    {
        return MicrosoftLiveDocumentSource.class;
    }
}
