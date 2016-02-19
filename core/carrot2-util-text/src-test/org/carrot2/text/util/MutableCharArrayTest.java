
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

package org.carrot2.text.util;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Test cases for {@link MutableCharArray}.
 */
public class MutableCharArrayTest extends CarrotTestCase
{
    @Test
    public void testCharArrayCharSequence()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss");
        assertEquals(seq.toString(), "Dawid Weiss");
    }

    @Test
    public void testCharArrayCharArrayIntInt()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss".toCharArray(), 1, 3);
        assertEquals("awi", seq.toString());
    }

    @Test
    public void testCharAt()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss".toCharArray(), 1, 3);
        assertEquals('a', seq.charAt(0));
        assertEquals('w', seq.charAt(1));
        assertEquals('i', seq.charAt(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCharAtOutOfBounds()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss".toCharArray(), 1, 3);
        assertEquals('-', seq.charAt(3));
    }

    @Test
    public void testLength()
    {
        MutableCharArray seq = new MutableCharArray("1234");
        assertEquals(4, seq.length());

        seq = new MutableCharArray("1234".toCharArray(), 1, 2);
        assertEquals(2, seq.length());
    }

    @Test
    public void testSubSequence()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss");
        assertEquals("wi", seq.subSequence(2, 4).toString());
    }

    @Test
    public void testEquals()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss");

        assertFalse(seq.equals("Dawid Weiss"));
        assertTrue(seq.subSequence(1, 5).equals(new MutableCharArray("awid")));
    }
    
    @Test
    public void testReset()
    {
        final MutableCharArray seq = new MutableCharArray("Dawid Weiss");
        assertEquals(seq.toString(), "Dawid Weiss");
        
        seq.reset("Abcdef");
        assertEquals(seq.toString(), "Abcdef");
        
        seq.reset("Dawid Weiss".toCharArray(), 1, 3);
        assertEquals(seq, new MutableCharArray("awi"));
        
        seq.reset("abc");
        int h1 = seq.hashCode();
        seq.reset("def");
        int h2 = seq.hashCode();
        assertTrue(h1 != h2);
    }
}
