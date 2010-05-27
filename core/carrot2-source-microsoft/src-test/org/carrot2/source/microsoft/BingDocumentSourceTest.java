
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

package org.carrot2.source.microsoft;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source.
 */
public class BingDocumentSourceTest extends
    MultipageDocumentSourceTestBase<BingDocumentSource>
{
    @Test
    public void testMarketOption() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());

        super.processingAttributes.put(
            AttributeUtils.getKey(BingDocumentSource.class, "market"), MarketOption.POLISH_POLAND);
        final int documentsReturned = runQuery("warszawa", 10);

        assertThat(documentsReturned).isGreaterThan(0);
        assertThat(getDocuments().get(0).getLanguage()).isEqualTo(LanguageCode.POLISH);
    }

    @Override
    public Class<BingDocumentSource> getComponentClass()
    {
        return BingDocumentSource.class;
    }

    @Override
    protected MultipageSearchEngineMetadata getSearchEngineMetadata()
    {
        return BingDocumentSource.metadata;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }
}
