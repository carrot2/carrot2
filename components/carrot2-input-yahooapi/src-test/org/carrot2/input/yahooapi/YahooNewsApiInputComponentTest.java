
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

public class YahooNewsApiInputComponentTest extends LocalInputComponentTestBase
{
    public YahooNewsApiInputComponentTest(String s)
    {
        super(s);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new YahooNewsApiInputComponent();
            }
        };
    }

    public void testMadonnaQuery() throws Exception
    {
        performQuery("Madonna", 100, new Range(75, 100));
    }

    public void testNewsSources() throws Exception
    {
        List results = query("Iraq", 100);

        Set set = new HashSet();
        for (Iterator it = results.iterator(); it.hasNext();)
        {
            final RawDocument rawDocument = (RawDocument) it.next();
            final String [] sources = (String []) rawDocument.getProperty(RawDocument.PROPERTY_SOURCES);
            assertNotNull(sources); 
            set.add(sources[0]);
        }

        assertTrue("At least one source expected.", set.size() >= 1);
    }

    public void testGuacamole() throws Exception
    {
        List results = query("guacamole", 100);
        assertTrue("At least one result expected.", results.size() >= 1);
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(YahooNewsApiInputComponentTest.class);
        }
        else
        {
            final TestSuite suite = new TestSuite();
            suite.setName(YahooNewsApiInputComponentTest.class.toString());
            return suite;
        }
    }
}
