package org.carrot2.text;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link CharSequenceIntMap}.
 */
public class CharSequenceIntMapTest
{
    @Test
    public void testGetIndexCharSequence()
    {
        final CharSequenceIntMap map = new CharSequenceIntMap();
        assertEquals(0, map.getIndex("a"));
        assertEquals(1, map.getIndex("b"));
        assertEquals(2, map.getIndex("c"));
        assertEquals(0, map.getIndex("a"));
        assertEquals(1, map.getIndex("b"));
    }

    @Test
    public void testGetIndexMutableCharArray()
    {
        final CharSequenceIntMap map = new CharSequenceIntMap();
        assertEquals(0, map.getIndex(new MutableCharArray("a")));
        assertEquals(1, map.getIndex(new MutableCharArray("b")));
        assertEquals(2, map.getIndex(new MutableCharArray("c")));
        assertEquals(0, map.getIndex(new MutableCharArray("a")));
        assertEquals(1, map.getIndex(new MutableCharArray("b")));
    }
    
    @Test
    public void testGetImages()
    {
        final CharSequenceIntMap map = new CharSequenceIntMap();
        assertEquals(0, map.getIndex("a"));
        assertEquals(1, map.getIndex("b"));
        assertEquals(2, map.getIndex("c"));
        assertEquals(0, map.getIndex("a"));
        assertEquals(1, map.getIndex("b"));
        
        final MutableCharArray [] tokens = map.getTokenImages();
        assertEquals("a", tokens[0].toString());
        assertEquals("b", tokens[1].toString());
        assertEquals("c", tokens[2].toString());
        assertEquals(3, tokens.length);
    }    
}
