
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

package org.carrot2.webapp.source;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.Test;

/**
 *
 */
public class WebDocumentSourceTest extends
    QueryableDocumentSourceTestBase<WebDocumentSource>
{
    @Override
    public Class<WebDocumentSource> getComponentClass()
    {
        return WebDocumentSource.class;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Test
    public void testFirstResults()
    {
        assumeTrue(externalApiTestsEnabled());

        runQuery("obama", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        assertThat(documents.size()).isGreaterThanOrEqualTo(8);
        for (int i = 0; i < 8; i++)
        {
            final List<String> sources = documents.get(i).getField(Document.SOURCES);
            assertThat(sources).as("sources[" + i + "]").contains("Google");
        }

        for (int i = 0; i < documents.size(); i++)
        {
            assertThat(documents.get(i).getId()).isEqualTo(i);
        }
    }
}
