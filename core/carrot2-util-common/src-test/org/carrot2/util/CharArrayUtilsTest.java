
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

import static org.carrot2.util.CharArrayUtils.hasCapitalizedLetters;
import static org.carrot2.util.CharArrayUtils.toLowerCaseCopy;
import static org.carrot2.util.CharArrayUtils.toLowerCaseInPlace;

import java.util.Arrays;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Ignore;
import org.junit.Test;

public class CharArrayUtilsTest extends CarrotTestCase
{
    @Test
    public void testToLowerCaseInPlace()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        assertSame(input2, toLowerCaseInPlace(input2));
        assertSame(input1, toLowerCaseInPlace(input1));
        assertArrayEquals(input1, input2);
    }

    @Test
    public void testToLowerCaseCopy()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        assertNotSame(input2, toLowerCaseCopy(input2));
        assertNotSame(input1, toLowerCaseCopy(input1));

        assertArrayEquals(input2, toLowerCaseCopy(input2));
        assertArrayEquals(input2, toLowerCaseCopy(input1));
    }

    @Test
    public void testHasCapitalizedLetters()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();
        char [] input3 = "Łódź".toCharArray();

        assertTrue(hasCapitalizedLetters(input1));
        assertFalse(hasCapitalizedLetters(input2));
        assertTrue(hasCapitalizedLetters(input3));
    }
    
    @Test
    public void testToLowerCaseBuffer()
    {
        char [] input1 = "ABC abc".toCharArray();
        char [] input2 = "abc abc".toCharArray();

        char [] buffer = new char [1024];
        
        assertTrue(CharArrayUtils.toLowerCase(input1, buffer));
        assertArrayEquals(input2, Arrays.copyOf(buffer, input1.length));

        assertFalse(CharArrayUtils.toLowerCase(input2, buffer));
        assertArrayEquals(input2, Arrays.copyOf(buffer, input2.length));
    }
    
    @Test
    public void testToLowerCaseBufferStartLength()
    {
        char [] input1 = "xyz ABC efg".toCharArray();
        char [] buffer = new char [3];
        
        assertTrue(CharArrayUtils.toLowerCase(input1, buffer, 4, 3));
        assertArrayEquals("abc".toCharArray(), buffer);
    }
    
    @Ignore // We don't compile with assertions on .NET.
    @Test(expected = AssertionError.class)
    public void bufferTooSmall()
    {
        char [] input1 = "xyz ABC efg".toCharArray();
        char [] buffer = new char [3];
        
        CharArrayUtils.toLowerCase(input1, buffer);
    }
    
    @Ignore // We don't compile with assertions on .NET.
    @Test(expected = AssertionError.class)
    public void wordTooShort()
    {
        char [] input1 = "xyz".toCharArray();
        char [] buffer = new char [3];
        
        CharArrayUtils.toLowerCase(input1, buffer, 0, 5);
    }
}
