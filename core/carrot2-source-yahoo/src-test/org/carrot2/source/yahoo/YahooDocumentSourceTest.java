
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

package org.carrot2.source.yahoo;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.carrot2.core.test.ExternalApiTestAssumptions.*;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests Yahoo! input component.
 */
public class YahooDocumentSourceTest extends
    MultipageDocumentSourceTestBase<YahooDocumentSource>
{
    @Test
    public void testNewsServiceSearch() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        initAttributes.put(AttributeUtils.getKey(YahooDocumentSource.class, "service"),
            YahooNewsSearchService.class);

        assertTrue(runQuery("iraq", 50) > 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewsThumbnails() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());

        initAttributes.put(AttributeUtils.getKey(YahooDocumentSource.class, "service"),
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
    @SuppressWarnings("unchecked")
    public void dumpResultAsJavaSource() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());

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
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return YahooSearchService.DEFAULT_METADATA;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
