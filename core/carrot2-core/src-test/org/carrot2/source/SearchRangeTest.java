package org.carrot2.source;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchRangeTest
{
    @Test
    public void testGetSearchRanges()
    {
        SearchRange [] range = SearchRange.getSearchRanges(0, 100, 1000, 50);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(50, range[1].start);
        assertEquals(50, range[1].results);


        range = SearchRange.getSearchRanges(0, 60, 1000, 50);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(50, range[1].start);
        assertEquals(10, range[1].results);

        range = SearchRange.getSearchRanges(10, 20, 1000, 50);
        assertEquals(1, range.length);
        assertEquals(10, range[0].start);
        assertEquals(20, range[0].results);

        range = SearchRange.getSearchRanges(10, 60, 1000, 50);
        assertEquals(2, range.length);
        assertEquals(10, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(60, range[1].start);
        assertEquals(10, range[1].results);
    }
}
