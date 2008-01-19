
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.pubmed;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

public class PubMedInputComponentTest extends LocalInputComponentTestBase
{
    final LocalComponentFactory inputFactory = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new PubMedInputComponent();
        }
    };

    public PubMedInputComponentTest(String s)
    {
        super(s);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return inputFactory;
    }

    public void testNoHitsQuery() throws Exception
    {
        performQuery("asdhasd alksjdhar swioer", 50, 0);
    }

    public void testSmallQuery() throws Exception
    {
        performQuery("test", 50, new Range(40, 50));
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("test", 100, new Range(80, 100));
    }

    public void testLargeQuery() throws Exception
    {
        performQuery("test", 400, new Range(320, 400));
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(PubMedInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
