
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

package org.carrot2.source.microsoft;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source.
 */
@UsesExternalServices
public class Bing3WebDocumentSourceTest extends
    MultipageDocumentSourceTestBase<Bing3WebDocumentSource>
{
    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }
    
    @Test
    public void testMarketOption() throws Exception
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing3DocumentSource.class, "market"), 
            MarketOption.POLISH_POLAND);

        final int documentsReturned = runQuery("warszawa", 10);

        assertThat(documentsReturned).isGreaterThan(0);
        assertThat(getDocuments().get(0).getLanguage()).isEqualTo(LanguageCode.POLISH);
    }

    @Test
    public void testSiteOption() throws Exception
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing3WebDocumentSource.class, "site"), 
            "put.poznan.pl");

        final int documentsReturned = runQuery("politechnika", 10);
        assertThat(documentsReturned).isGreaterThan(0);
        for (Document doc : getDocuments()) {
            assertThat(doc.getContentUrl()).contains("put.poznan.pl");
        }
    }

    @Test
    public void testPdfFileType() throws Exception
    {
        final int documentsReturned = runQuery("cats filetype:pdf", 100);
        assertThat(documentsReturned).isGreaterThan(0);
        boolean hadPdfs = false;
        for (Document doc : getDocuments())
        {
            String url = doc.getContentUrl().toLowerCase();
            System.out.println(url);
            hadPdfs |= url.contains(".pdf");
        }
        assertTrue(hadPdfs);
    }

    @Test
    public void testDocFileType() throws Exception
    {
        final int documentsReturned = runQuery("cats filetype:doc", 100);
        assertThat(documentsReturned).isGreaterThan(0);
        boolean hadDocs = false;
        for (Document doc : getDocuments())
        {
            String url = doc.getContentUrl().toLowerCase();
            System.out.println(url);
            hadDocs |= url.contains(".doc");
        }
        assertTrue(hadDocs);
    }

    @Override
    public Class<Bing3WebDocumentSource> getComponentClass()
    {
        return Bing3WebDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return Bing3WebDocumentSource.metadata;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
