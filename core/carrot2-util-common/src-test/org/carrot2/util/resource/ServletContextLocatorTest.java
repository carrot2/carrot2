
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import javax.servlet.ServletContext;

import org.carrot2.util.tests.CarrotTestCase;
import org.easymock.EasyMock;
import org.junit.Test;

public class ServletContextLocatorTest extends CarrotTestCase
{
    @Test
    public void testHashCodeEqualsServletContextLocator() 
    {
        ServletContext context = EasyMock.createMock(ServletContext.class);

        ResourceLookupTest.checkHashEquals(
            new ServletContextLocator(context),
            new ServletContextLocator(context));
    }
}
