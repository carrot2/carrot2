
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

package org.carrot2.source.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.ContextClassLoaderLocator;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.URLResourceWithParams;
import org.junit.Test;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Test cases for {@link XmlDocumentSource}.
 */
public class XmlDocumentSourceTest extends DocumentSourceTestBase<XmlDocumentSource>
{
    /**
     * Resource locator for test resources, use context class loader.
     */
    private ResourceLookup resourceLocator = new ResourceLookup(
        new ContextClassLoaderLocator());

    /**
     * Transforms {@link Document}s to their ids.
     */
    protected static final Function<Document, Integer> DOCUMENT_TO_INT_ID = new Function<Document, Integer>()
    {
        public Integer apply(Document document)
        {
            return Integer.valueOf(document.getStringId());
        }
    };

    @Override
    public Class<XmlDocumentSource> getComponentClass()
    {
        return XmlDocumentSource.class;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadClusters()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-with-clusters.xml");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"), xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "readClusters"), true);
        runQuery();

        List<Cluster> clusters = (List<Cluster>) resultAttributes.get(AttributeNames.CLUSTERS);
        assertThat(clusters).isNotEmpty();
    }

    @Test
    public void testLegacyXml()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-apple-computer.xml");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "results"),
            300);
        final int documentCount = runQuery();
        assertEquals(200, documentCount);
        assertEquals("apple computer", resultAttributes.get(AttributeNames.QUERY));
    }

    @Test
    public void testResultsTruncation()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-apple-computer.xml");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeNames.RESULTS, 50);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "readAll"), 
            false);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals("apple computer", resultAttributes.get(AttributeNames.QUERY));
    }

    @Test
    public void testXsltNoParameters()
    {
        IResource xml = resourceLocator.getFirst("/xml/custom-parameters-not-required.xml");
        IResource xslt = resourceLocator.getFirst("/xsl/custom-xslt.xsl");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            xslt);
        final int documentCount = runQuery();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testXsltWithParameters()
    {
        IResource xml = resourceLocator.getFirst("/xml/custom-parameters-required.xml");
        IResource xslt = resourceLocator.getFirst("/xsl/custom-xslt.xsl");

        Map<String, String> xsltParameters = Maps.newHashMap();
        xsltParameters.put("id-field", "number");
        xsltParameters.put("title-field", "heading");
        xsltParameters.put("snippet-field", "snippet");
        xsltParameters.put("url-field", "url");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            xslt);
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
            "xsltParameters"), xsltParameters);
        final int documentCount = runQuery();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testNoIdsInSourceXml()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-no-ids.xml");

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        final int documentCount = runQuery();
        assertEquals(2, documentCount);
        assertEquals(Lists.newArrayList(0, 1), Lists.transform(getDocuments(),
            DOCUMENT_TO_INT_ID));
    }

    @Test
    public void testGappedIdsInSourceXml()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-gapped-ids.xml");
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"), xml);
        try {
            runQuery();
            fail();
        } catch (ProcessingException e) {
            assertThat(e.getMessage()).contains("Null identifiers cannot be mixed with");
        }
    }

    @Test
    public void testDuplicatedIdsInSourceXml()
    {
        IResource xml = resourceLocator.getFirst("/xml/carrot2-duplicated-ids.xml");
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"), xml);
        try {
            runQuery();
            fail();
        } catch (ProcessingException e) {
            assertThat(e.getMessage()).contains("Identifiers must be unique");
        }
    }

    @Test
    public void testInitializationTimeXslt()
    {
        IResource xslt = resourceLocator.getFirst("/xsl/custom-xslt.xsl");
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"), xslt);

        IResource xml = resourceLocator.getFirst("/xml/custom-parameters-not-required.xml");
        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);

        final int documentCount = runQueryInCachingController();
        assertTransformedDocumentsEqual(documentCount);
    }

    @Test
    public void testOverridingInitializationTimeXslt()
    {
        IResource initXslt = resourceLocator.getFirst("/xsl/carrot2-identity.xsl");
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            initXslt);
        
        @SuppressWarnings("unchecked")
        Controller controller = getCachingController(initAttributes);

        // Run with identity XSLT
        {
            IResource xml = resourceLocator.getFirst("/xml/carrot2-test.xml");
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }

        // Run with swapping XSLT
        {
            IResource xml = resourceLocator.getFirst("/xml/carrot2-test.xml");
            IResource xslt = resourceLocator.getFirst("/xsl/carrot2-title-snippet-switch.xsl");
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);
            processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
                "xslt"), xslt);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }
    }

    @Test
    public void testDisablingInitializationTimeXslt()
    {
        IResource initXslt = resourceLocator.getFirst(
            "/xsl/carrot2-title-snippet-switch.xsl");
        initAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xslt"),
            initXslt);

        @SuppressWarnings("unchecked")
        Controller controller = getCachingController(initAttributes);
        
        // Run with swapping XSLT
        {
            IResource xml = resourceLocator.getFirst("/xml/carrot2-test.xml");
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }

        // Run without XSLT
        {
            IResource xml = resourceLocator.getFirst("/xml/carrot2-test.xml");
            processingAttributes.put(AttributeUtils
                .getKey(XmlDocumentSource.class, "xml"), xml);
            processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class,
                "xslt"), null);

            final int documentCount = runQuery(controller);
            assertEquals(2, documentCount);
            assertEquals(Lists.newArrayList("Title 0", "Title 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_TITLE));
            assertEquals(Lists.newArrayList("Snippet 0", "Snippet 1"), Lists.transform(
                getDocuments(), DOCUMENT_TO_SUMMARY));
        }
    }

    @Test
    public void testRemoteUrl() throws MalformedURLException
    {
        String base = System.getProperty("carrot2.xml.feed.url.base");
        assumeTrue("carrot2.xml.feed.url.base property undefined.", !Strings.isNullOrEmpty(base));

        IResource xml = new URLResourceWithParams(new URL(base + "&q=${query}&results=${results}"));
        final String query = "apple computer";

        processingAttributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"),
            xml);
        processingAttributes.put(AttributeNames.QUERY, query);
        processingAttributes.put(AttributeNames.RESULTS, 50);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals(query, resultAttributes.get(AttributeNames.QUERY));
    }

    private void assertTransformedDocumentsEqual(final int documentCount)
    {
        assertEquals(2, documentCount);
        assertEquals("xslt test", resultAttributes.get(AttributeNames.QUERY));
        assertEquals(Lists.newArrayList(498967, 831478), Lists.transform(getDocuments(),
            DOCUMENT_TO_INT_ID));
        assertEquals(Lists.newArrayList("IBM's MARS Block Cipher.",
            "IBM WebSphere Studio Device Developer"), Lists.transform(getDocuments(),
            DOCUMENT_TO_TITLE));
        assertEquals(Lists.newArrayList(
            "The company's AES proposal using 128 bit blocks.",
            "An integrated development environment."), Lists.transform(getDocuments(),
            DOCUMENT_TO_SUMMARY));
        assertEquals(Lists.newArrayList("http://www.research.ibm.com/security/mars.html",
            "http://www-3.ibm.com/software/wireless/wsdd/"), Lists.transform(
            getDocuments(), DOCUMENT_TO_CONTENT_URL));
    }
}
