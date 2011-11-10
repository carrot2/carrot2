
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

import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source (news).
 */
@UsesExternalServices
public class Bing2ImageDocumentSourceTest extends DocumentSourceTestBase<Bing2ImageDocumentSource>
{
    @Override
    public Class<Bing2ImageDocumentSource> getComponentClass()
    {
        return Bing2ImageDocumentSource.class;
    }

    @Test
    public void testCatsQuery()
    {
        System.out.println("baha");
        assertThat(runQuery("cats", 25)).isGreaterThan(20);
    }
}
