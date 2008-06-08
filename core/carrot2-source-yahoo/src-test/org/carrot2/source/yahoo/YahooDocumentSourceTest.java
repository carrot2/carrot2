package org.carrot2.source.yahoo;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.MultipartDocumentSourceTestBase;
import org.carrot2.source.SearchEngineMetadata;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Yahoo! input component.
 */
@RunWith(AnnotationRunner.class)
public class YahooDocumentSourceTest extends
    MultipartDocumentSourceTestBase<YahooDocumentSource>
{
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNewsServiceSearch() throws Exception
    {
        initAttributes.put(YahooDocumentSource.class.getName() + ".service",
            YahooNewsSearchService.class);

        assertTrue(runQuery("iraq", 50) > 0);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void testNewsThumbnails() throws Exception
    {
        initAttributes.put(YahooDocumentSource.class.getName() + ".service",
            YahooNewsSearchService.class);

        assertTrue(runQuery("world", 200) > 0);
        List<Document> documents = (List<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS);
        
        // At least one result should have a thumbnail
        int thumbnailCount = 0;
        for (Document document : documents)
        {
            if (document.getField(Document.THUMBNAIL_URL) != null)
            {
                thumbnailCount++;
            }
        }
        
        assertThat(thumbnailCount).isGreaterThan(0);
    }

    @Ignore
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void dumpResultAsJavaSource() throws Exception
    {
        runQuery("data mining", 100);

        Collection<Document> docs = (Collection<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS);
        for (Document d : docs)
        {
            System.out.println("{ \""
                + StringEscapeUtils.escapeJava((String) d.getField(Document.CONTENT_URL))
                + "\",\n\t" + "\""
                + StringEscapeUtils.escapeJava((String) d.getField(Document.TITLE))
                + "\",\n\t" + "\""
                + StringEscapeUtils.escapeJava((String) d.getField(Document.SUMMARY))
                + "\" },\n\n");
        }
    }

    @Override
    public Class<YahooDocumentSource> getComponentClass()
    {
        return YahooDocumentSource.class;
    }

    @Override
    protected SearchEngineMetadata getSearchEngineMetadata()
    {
        return YahooSearchService.DEFAULT_METADATA;
    }
}
