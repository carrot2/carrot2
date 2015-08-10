
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.source;

import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Maps;

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

    @UsesExternalServices
    @Test
    @Override
    public void testLargeQuery() throws Exception
    {
        runAndCheckMinimumResults(getLargeQueryText(), getLargeQuerySize(), 75);
    }

    @UsesExternalServices
    @Test
    public void testFirstResults()
    {
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
            assertThat(Integer.valueOf(documents.get(i).getStringId())).isEqualTo(i);
        }
    }
    
    @UsesExternalServices
    @Test
    public void testRequestIndependence() 
    {
        @SuppressWarnings("unchecked")
        final Controller controller = ControllerFactory.createCachingPooling(
            org.carrot2.core.IDocumentSource.class);
        closeAfterTest(controller);

        final Map<String,Object> attrs = Maps.newHashMap();

        CommonAttributesDescriptor.attributeBuilder(attrs)
            .results(50)
            .query("data mining");
        
        controller.process(attrs,
            org.carrot2.webapp.source.WebDocumentSource.class, LingoClusteringAlgorithm.class);               

        attrs.clear();
        CommonAttributesDescriptor.attributeBuilder(attrs)
            .results(50)
            .query(WebDocumentSource.QUERY_FAILURE);

        try {
            controller.process(attrs,
                org.carrot2.webapp.source.WebDocumentSource.class, LingoClusteringAlgorithm.class);
            fail();
        } catch (ProcessingException e) {
            assertThat(e.getCause().getMessage()).contains("Synthetic failure");
        }
    }
}
