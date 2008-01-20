
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

package org.carrot2.input.etools;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

/**
 * eTools input component tests.
 *
 * @author Stanislaw Osinski
 */
public class EToolsLocalInputComponentTest extends LocalInputComponentTestBase
{
    public EToolsLocalInputComponentTest(String name)
    {
        super(name);
    }

    final LocalComponentFactory inputFactory = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new EToolsLocalInputComponent("Carrot2");
        }
    };

    protected LocalComponentFactory getLocalInputFactory()
    {
        return inputFactory;
    }

    public void testNoHitsQuery() throws Exception
    {
        performQuery("asdhfasd aleksjdhar swigoer", 50, 0);
    }

    public void testSmallQuery() throws Exception
    {
        performQuery("test", 50, 50);
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("test", 100, new Range(75, 100));
    }

    public void testLargeQuery() throws Exception
    {
        performQuery("test", 400, new Range(150, 400));
    }

    /**
     * Make sure that the results contain information about the source.
     *
     * @throws Exception
     */
    public void testSources() throws Exception
    {
        List results = query("test", 50);

        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument document = (RawDocument) it.next();
            String [] sources = (String []) document
                .getProperty(RawDocument.PROPERTY_SOURCES);

            assertNotNull("Sources information available", sources);
            assertTrue("Non-zero number of sources", sources.length > 0);
        }
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(EToolsLocalInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
