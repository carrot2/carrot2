
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

package org.carrot2.util;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link ExceptionUtils}.
 */
@SuppressWarnings("serial")
public class ExceptionUtilsTest extends CarrotTestCase
{
    public static class ExceptionA extends Throwable
    {
        public ExceptionA()
        {
            throw new RuntimeException("Don't call this constructor.");
        }

        public ExceptionA(Throwable t)
        {
            super(t);
        }
    }

    public static class ExceptionB extends Exception
    {
    }

    public static class ExceptionC extends Exception
    {
    }

    public static class ExceptionD extends Exception
    {
        public ExceptionD(String s)
        {
            super(s);
        }
    }

    public static class ExceptionE extends Exception
    {
        public ExceptionE(String s, String s2)
        {
            super(s);
        }
    }

    @Test
    public void testWrappedWithConstructor()
    {
        Throwable t = new ExceptionB();
        Throwable w = ExceptionUtils.wrapAs(ExceptionA.class, t);
        
        Assert.assertTrue(w instanceof ExceptionA);
        Assert.assertEquals(t, w.getCause());
    }

    @Test
    public void testWrappedWithInitCause()
    {
        Throwable t = new ExceptionB();
        Throwable w = ExceptionUtils.wrapAs(ExceptionC.class, t);
        
        Assert.assertTrue(w instanceof ExceptionC);
        Assert.assertEquals(t, w.getCause());
    }
    @Test
    public void testWrappedWithStingConstructor()
    {
        Throwable t = new ExceptionC();
        ExceptionD w = ExceptionUtils.wrapAs(ExceptionD.class, t);

        Assert.assertTrue(w instanceof ExceptionD);
        Assert.assertEquals(t, w.getCause());
    }

    @Test
    public void testNotWrappedAtAll()
    {
        Throwable t = new ExceptionB();
        Throwable w = ExceptionUtils.wrapAs(ExceptionB.class, t);

        Assert.assertTrue(w == t);
    }

    @Test(expected = RuntimeException.class)
    public void testNoMatchingWrapperFound()
    {
        Throwable t = new ExceptionB();
        ExceptionUtils.wrapAs(ExceptionE.class, t);
    }
}
