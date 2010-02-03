
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

package org.carrot2.source.google;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.Test;

/**
 * Test cases for {@link GoogleDocumentSource}.
 */
public class GoogleDocumentSourceTest extends
    MultipageDocumentSourceTestBase<GoogleDocumentSource>
{
    @Override
    public Class<GoogleDocumentSource> getComponentClass()
    {
        return GoogleDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return GoogleDocumentSource.metadata;
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 32;
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 16;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Test
    public void testHighlightsRemoved()
    {
        assumeTrue(externalApiTestsEnabled());

        runQuery("test", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        for (Document document : documents)
        {
            final String snippet = document.getField(Document.SUMMARY);
            assertThat(snippet).doesNotMatch(".*</?b>.*");
        }
    }

    @Test
    public void testNoUrlEncodingInUrls()
    {
        assumeTrue(externalApiTestsEnabled());
        runQuery("help", getSmallQuerySize());

        final List<Document> documents = getDocuments();
        for (Document document : documents)
        {
            final String url = document.getField(Document.CONTENT_URL);
            assertThat(url).doesNotMatch(".*%3[Ff].*");
        }
    }
}
