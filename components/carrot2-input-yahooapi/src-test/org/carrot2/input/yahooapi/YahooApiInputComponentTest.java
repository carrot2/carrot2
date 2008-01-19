
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

package org.carrot2.input.yahooapi;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

public class YahooApiInputComponentTest extends LocalInputComponentTestBase
{
    public YahooApiInputComponentTest(String s)
    {
        super(s);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new YahooApiInputComponent();
            }
        };
    }

    public void testJanWeglarzQuery() throws Exception
    {
        performQuery("Jan Węglarz", 100, new Range(90, 100));
    }

    /**
     * This is a test case for a situation where a large enough number of results is
     * requested to spawn more than one fetching thread, but only very little results are
     * available. As a result -- the returned list will contain the handful of snippets
     * duplicated by the number of fetching threads (because each thread downloaded the
     * same results).
     */
    public void testSiteQuery() throws Exception
    {
        List results = query("koelle bmw site:handelsblatt.de", 400);

        Set set = new HashSet();
        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            set.add(rawDocument.getUrl());
        }

        assertEquals("Number of unique URLs not equal to number of results", set.size(),
            results.size());
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("dawid weiss ant styler docbook poznan", 100, new Range(1, 100));
    }

    public void testStartPositionIncorrect() throws Exception
    {
        performQuery("webstart splash colors", 100, new Range(1, 100));
    }

    public void testNoResults() throws Exception
    {
        performQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100, 0);
    }

    public void testResultsRequested() throws Exception
    {
        performQuery("apache", 50, 50);
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(YahooApiInputComponentTest.class);
        }
        else
        {
            final TestSuite suite = new TestSuite();
            suite.setName(YahooApiInputComponentTest.class.toString());
            return suite;
        }
    }
}
