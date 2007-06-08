/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.opensearch;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.test.LocalInputComponentTestBase;

/**
 * A test case for OpenSearch input component.
 *
 * @author Dawid Weiss
 */
public class IndeedOpenSearchInputComponentTest extends LocalInputComponentTestBase
{
    public IndeedOpenSearchInputComponentTest(String s)
    {
        super(s);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                final OpenSearchInputComponent input = new OpenSearchInputComponent(
                    "http://www.indeed.com/opensearch?q={searchTerms}&start={startIndex}&limit={count}");
                input.setMaxResults(50);
                return input;
            }
        };
    }

    public void testIndeed() throws Exception
    {
        performQuery("programmer", 100, 50);
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(IndeedOpenSearchInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
