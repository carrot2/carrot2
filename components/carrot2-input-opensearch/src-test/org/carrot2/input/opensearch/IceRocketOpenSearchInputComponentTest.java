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
import org.carrot2.core.test.Range;

/**
 * A test case for OpenSearch input component.
 *
 * @author Dawid Weiss
 */
public class IceRocketOpenSearchInputComponentTest extends LocalInputComponentTestBase
{
    public IceRocketOpenSearchInputComponentTest(String name)
    {
        super(name);
    }

    protected LocalComponentFactory getLocalInputFactory()
    {
        return new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new OpenSearchInputComponent(
                    "http://blogs.icerocket.com/search?q={searchTerms}&rss=1&os=1&p={startPage}&n={count}");
            }
        };
    }

    public void testIceRocket() throws Exception
    {
        performQuery("blog", 65, new Range(40, 65));
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(IceRocketOpenSearchInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
