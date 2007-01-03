
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

package org.carrot2.util;

import java.util.*;

import junit.framework.Assert;
import junitx.framework.*;

/**
 * @author Stanislaw Osinski
 */
public class Asserts
{
    /**
     * @param message
     * @param expected
     * @param actual
     */
    static public void assertEquals(String message, char [][] expected,
        char [][] actual)
    {
        if (Arrays.equals(expected, actual))
        {
            return;
        }

        String formatted = "";
        if (message != null)
        {
            formatted = message + " ";
        }

        Assert.assertNotNull(formatted
            + "expected array: <not null> but was <null>", expected);
        Assert.assertNotNull(formatted
            + "expected array: <null> but was <not null>", actual);
        Assert.assertEquals(formatted + "[array length] ", expected.length,
            actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            ArrayAssert.assertEquals(formatted + "[position " + i + "]",
                expected[i], actual[i]);
        }
    }

    /**
     * @param message
     * @param expected
     * @param actual
     */
    static public void assertEquals(String message, int [][] expected,
        int [][] actual)
    {
        if (Arrays.equals(expected, actual))
        {
            return;
        }

        String formatted = "";
        if (message != null)
        {
            formatted = message + " ";
        }

        Assert.assertNotNull(formatted
            + "expected array: <not null> but was <null>", expected);
        Assert.assertNotNull(formatted
            + "expected array: <null> but was <not null>", actual);
        Assert.assertEquals(formatted + "[array length] ", expected.length,
            actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            ArrayAssert.assertEquals(formatted + "[position " + i + "]",
                expected[i], actual[i]);
        }
    }
    
    /**
     * @param message
     * @param expected
     * @param actual
     */
    static public void assertEquals(String message, Object [][] expected,
        Object [][] actual)
    {
        if (Arrays.equals(expected, actual))
        {
            return;
        }
        
        String formatted = "";
        if (message != null)
        {
            formatted = message + " ";
        }
        
        Assert.assertNotNull(formatted
            + "expected array: <not null> but was <null>", expected);
        Assert.assertNotNull(formatted
            + "expected array: <null> but was <not null>", actual);
        Assert.assertEquals(formatted + "[array length] ", expected.length,
            actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            ArrayAssert.assertEquals(formatted + "[position " + i + "]",
                expected[i], actual[i]);
        }
    }
}
