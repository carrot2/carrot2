
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
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source (news).
 */
@UsesExternalServices
public class Bing3ImageDocumentSourceTest extends
    DocumentSourceTestBase<Bing3ImageDocumentSource>
{
    @Override
    public Class<Bing3ImageDocumentSource> getComponentClass()
    {
        return Bing3ImageDocumentSource.class;
    }

    @Test
    public void testCatsQuery()
    {
        assertThat(runQuery("cats", 30)).isGreaterThan(20);

        int withThumbnail = 0;
        for (Document doc : result.getDocuments())
        {
            if (doc.getField(Document.THUMBNAIL_URL) != null)
            {
                withThumbnail++;
            }
        }
        assertThat(withThumbnail).isGreaterThan(result.getDocuments().size() / 2);
    }
}
