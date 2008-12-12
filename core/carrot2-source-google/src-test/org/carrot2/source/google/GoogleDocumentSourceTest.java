
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.google;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link GoogleDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
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
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testHighlightsRemoved()
    {
        runQuery("test", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        for (Document document : documents)
        {
            final String snippet = document.getField(Document.SUMMARY);
            assertThat(snippet).doesNotMatch(".*</?b>.*");
        }
    }
}
