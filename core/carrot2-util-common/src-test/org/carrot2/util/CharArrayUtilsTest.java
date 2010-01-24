
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CharArrayUtilsTest
{
    @Test
    public void testToLowerCaseInPlace()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        assertSame(input2, CharArrayUtils.toLowerCaseInPlace(input2));
        assertSame(input1, CharArrayUtils.toLowerCaseInPlace(input1));
        assertArrayEquals(input1, input2);
    }

    @Test
    public void testToLowerCaseCopy()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        assertNotSame(input2, CharArrayUtils.toLowerCaseCopy(input2));
        assertNotSame(input1, CharArrayUtils.toLowerCaseCopy(input1));

        assertArrayEquals(input2, CharArrayUtils.toLowerCaseCopy(input2));
        assertArrayEquals(input2, CharArrayUtils.toLowerCaseCopy(input1));
    }

    @Test
    public void testHasCapitalizedLetters()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();
        char [] input3 = "Łódź".toCharArray();

        assertTrue(CharArrayUtils.hasCapitalizedLetters(input1));
        assertFalse(CharArrayUtils.hasCapitalizedLetters(input2));
        assertTrue(CharArrayUtils.hasCapitalizedLetters(input3));
    }
    
    @Test
    public void testToLowerCaseBuffer()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        char [] buffer = new char [1024];
        
        assertTrue(CharArrayUtils.toLowerCase(input1, buffer));
        assertArrayEquals(input2, CharArrayUtils.copyOf(buffer, 0, input1.length));

        assertFalse(CharArrayUtils.toLowerCase(input2, buffer));
        assertArrayEquals(input2, CharArrayUtils.copyOf(buffer, 0, input2.length));
    }
}
