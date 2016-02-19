
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
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source (news).
 */
@UsesExternalServices
public class Bing3NewsDocumentSourceTest extends DocumentSourceTestBase<Bing3NewsDocumentSource>
{
    @Override
    public Class<Bing3NewsDocumentSource> getComponentClass()
    {
        return Bing3NewsDocumentSource.class;
    }

    @Test
    public void testPresidentQuery()
    {
        assertThat(runQuery("president", 50)).isGreaterThan(5);

        int withSources = 0;
        for (Document doc : result.getDocuments()) {
            if (doc.getSources() != null && !doc.getSources().isEmpty()) {
                withSources++;
            }
        }
        assertThat(withSources).isGreaterThan(result.getDocuments().size() / 2);
    }
    
    @Test
    public void testNewsCategory()
    {
        super.processingAttributes.put(
            AttributeUtils.getKey(Bing3NewsDocumentSource.class, "newsCategory"), 
            NewsCategory.SPORTS);

        assertThat(runQuery("golf", 10)).isGreaterThan(5);
    }

}
