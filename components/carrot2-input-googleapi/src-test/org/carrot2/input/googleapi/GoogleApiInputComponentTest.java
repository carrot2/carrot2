
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

package org.carrot2.input.googleapi;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

public class GoogleApiInputComponentTest extends LocalInputComponentTestBase
{
    private final GoogleKeysPool keysPool;

    public GoogleApiInputComponentTest(String testName, GoogleKeysPool pool) throws IOException
    {
        super(testName);
        keysPool = pool;
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new GoogleApiInputComponent(keysPool);
            }
        };
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("dawid weiss ant styler docbook poznan", 100, new Range(1, 100));
    }

    public void testEmptyResults() throws Exception
    {
        performQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100, 0);
    }

    public void testResultsRequested() throws Exception
    {
        performQuery("apache", 50, 50);
    }

    public void testEntities() throws Exception
    {
        List results = query("Ala ma kota", 100);

        assertTrue("Non empty results list", results.size() > 0);

        for (Iterator i = results.iterator(); i.hasNext();)
        {
            RawDocument rd = (RawDocument) i.next();

            final String titleSummary = (rd.getTitle() + " " + rd.getSnippet());
            Logger.getRootLogger().info(titleSummary);
            assertTrue(titleSummary.indexOf("&gt;") < 0);
            assertTrue(titleSummary.indexOf("&lt;") < 0);
        }
    }

    public static Test suite() throws IOException
    {
        TestSuite testSuite = new TestSuite();
        testSuite.setName(GoogleApiInputComponentTest.class.toString());

        if (isApiTestingEnabled())
        {
            final GoogleKeysPool pool = new GoogleKeysPool();
            pool.addKeys(new File("keypool"), ".key");

            if (pool.getKeysTotal() == 0)
            {
                log.warn("No available google api keys, skipping the tests.");
            }
            else
            {
                final String [] testNames = new String [] {
                    "testMediumQuery",
                    "testEmptyResults",
                    "testResultsRequested",
                    "testEntities"
                };

                for (int i = 0; i < testNames.length; i++)
                {
                    testSuite.addTest(new GoogleApiInputComponentTest(testNames[i], pool));
                }
            }
        }

        return testSuite;
    }
}
