
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.etools;

import java.util.List;
import java.util.Set;

import org.carrot2.core.Document;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.source.SearchEngineBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.tests.UsesExternalServices;
import org.fest.assertions.MapAssert;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Test cases for {@link EToolsDocumentSource}.
 */
@UsesExternalServices
public class EToolsDocumentSourceTest extends
    QueryableDocumentSourceTestBase<EToolsDocumentSource>
{
    @Override
    public Class<EToolsDocumentSource> getComponentClass()
    {
        return EToolsDocumentSource.class;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Test
    public void testDataSources() throws Exception
    {
        runQuery("apache", 50);

        final Set<String> sources = Sets.newHashSet();
        for (Document document : getDocuments())
        {
            final List<String> documentSources = document.getField(Document.SOURCES);
            assertThat(documentSources).isNotEmpty();
            sources.addAll(documentSources);
        }

        assertThat(sources.size()).isGreaterThanOrEqualTo(2);
    }
    
    @UsesExternalServices
    @Test
    @Override
    public void testLargeQuery() throws Exception
    {
        runAndCheckMinimumResults(getLargeQueryText(), getLargeQuerySize(), 75);
    }

    @Test
    public void testGzipCompression() throws Exception
    {
        runQuery("apache", 50);
        assertThat(resultAttributes).includes(
            MapAssert.entry(AttributeUtils.getKey(SearchEngineBase.class, "compressed"),
                true));
    }

    @Test
    public void testDataSourceResultsCount()
    {
        final EToolsDocumentSource source = new EToolsDocumentSource();

        checkDataSourceResultsCount(source, 0, 0);
        checkDataSourceResultsCount(source, 50, 20);
        checkDataSourceResultsCount(source, 100, 20);
        checkDataSourceResultsCount(source, 120, 30);
        checkDataSourceResultsCount(source, 200, 30);
        checkDataSourceResultsCount(source, 250, 40);
        checkDataSourceResultsCount(source, 400, 40);
        checkDataSourceResultsCount(source, 450, 40);
        checkDataSourceResultsCount(source, 1000, 40);
    }

    private void checkDataSourceResultsCount(EToolsDocumentSource source, int results,
        int expectedDataSourceResultsCount)
    {
        source.results = results;
        assertEquals("Data source results count", expectedDataSourceResultsCount, source
            .getDataSourceResultsCount());
    }
}
