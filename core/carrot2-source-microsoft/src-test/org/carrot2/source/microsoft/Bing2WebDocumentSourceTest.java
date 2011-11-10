
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
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
public class Bing2WebDocumentSourceTest extends
    MultipageDocumentSourceTestBase<Bing2WebDocumentSource>
{
    @Test
    public void testMarketOption() throws Exception
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing2DocumentSource.class, "market"), 
            MarketOption.POLISH_POLAND);

        final int documentsReturned = runQuery("warszawa", 10);

        assertThat(documentsReturned).isGreaterThan(0);
        assertThat(getDocuments().get(0).getLanguage()).isEqualTo(LanguageCode.POLISH);
    }

    @Test
    public void testSiteOption() throws Exception
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing2WebDocumentSource.class, "site"), 
            "www.cs.put.poznan.pl");

        final int documentsReturned = runQuery("weiss", 10);
        assertThat(documentsReturned).isGreaterThan(0);
        for (Document doc : getDocuments()) {
            assertThat(doc.getContentUrl()).contains("www.cs.put.poznan.pl");
        }
    }

    @Test
    public void testFileTypeOption() throws Exception
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing2WebDocumentSource.class, "fileTypes"), 
            "pdf doc");

        final int documentsReturned = runQuery("cats", 10);
        assertThat(documentsReturned).isGreaterThan(0);
        boolean hadPdfs = false;
        boolean hadDocs = false;
        for (Document doc : getDocuments())
        {
            String url = doc.getContentUrl().toLowerCase();
            hadPdfs |= url.contains(".pdf");
            hadDocs |= url.contains(".doc");
        }
        assertTrue(hadPdfs);
        assertTrue(hadDocs);
    }

    @Override
    public Class<Bing2WebDocumentSource> getComponentClass()
    {
        return Bing2WebDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return Bing2WebDocumentSource.metadata;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
