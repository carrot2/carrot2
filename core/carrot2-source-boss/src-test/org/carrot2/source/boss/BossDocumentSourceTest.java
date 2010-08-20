
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

package org.carrot2.source.boss;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.junit.Test;

/**
 * Tests Yahoo Boss {@link IDocumentSource}.
 */
public class BossDocumentSourceTest extends
    MultipageDocumentSourceTestBase<BossDocumentSource>
{
    @Override
    public Class<BossDocumentSource> getComponentClass()
    {
        return BossDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return BossSearchService.DEFAULT_METADATA;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Override
    protected double slack()
    {
        return 1.5;
    }

    @Test
    public void testWbrRemoval()
    {
        assumeTrue(externalApiTestsEnabled());
        runQuery("marillenbaum krankheit", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        int i = 0;
        for (Document document : documents)
        {
            assertThat(document).as("doc[" + i++ + "]").stringFieldsDoNotMatchPattern(
                ".*<wbr>.*");
        }
    }
}
