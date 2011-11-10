
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
public class Bing2NewsDocumentSourceTest extends DocumentSourceTestBase<Bing2NewsDocumentSource>
{
    @Override
    public Class<Bing2NewsDocumentSource> getComponentClass()
    {
        return Bing2NewsDocumentSource.class;
    }

    @Test
    public void testPresidentQuery()
    {
        assertThat(runQuery("president", 50)).isGreaterThan(5);
    }
}
