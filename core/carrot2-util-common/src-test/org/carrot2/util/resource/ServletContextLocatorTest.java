package org.carrot2.util.resource;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.junit.Test;

public class ServletContextLocatorTest
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
