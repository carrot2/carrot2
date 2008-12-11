
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

package org.carrot2.input.msnapi;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.test.LocalInputComponentTestBase;
import org.carrot2.core.test.Range;

/**
 * Test {@link MsnApiInputComponent}.
 *
 * @author Dawid Weiss
 */
public class MsnApiInputComponentTest extends LocalInputComponentTestBase
{
    public MsnApiInputComponentTest(String s)
    {
        super(s);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new MsnApiInputComponent();
            }
        };
    }

    public void testLargeQuery() throws Exception
    {
        performQuery("windows", 200, new Range(170, 200));
    }

    public void testResultsRequested() throws Exception
    {
        performQuery("test", 150, new Range(90, 150));
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("dawid weiss ant styler docbook poznan", 100, new Range(1, 100));
    }

    public void testEmptyQuery() throws Exception
    {
        performQuery("duiogig oiudgisugvi\u0078 siug iugw iusviuwg", 100, 0);
    }

    public void testStartFromBug() throws Exception
    {
        List results = query("clustering",
            MsnApiInputComponent.MAXIMUM_RESULTS_PERQUERY * 2);

        // the results should contain some documents.
        for (int i = 0; i < results.size() / 2; i++)
        {
            final String summary = ((RawDocument) results.get(i)).getSnippet() + "";
            final String summaryOffset = ((RawDocument) results.get(i + results.size()
                / 2)).getSnippet()
                + "";

            if (!summary.equals(summaryOffset))
            {
                return;
            }
        }

        fail();
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(MsnApiInputComponentTest.class);
        }
        else
        {
            final TestSuite suite = new TestSuite();
            suite.setName(MsnApiInputComponentTest.class.toString());
            return suite;
        }
    }
}