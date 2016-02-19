
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

package org.carrot2.source;

import org.carrot2.source.MultipageSearchEngine.SearchRange;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

public class SearchRangeTest extends CarrotTestCase
{
    @Test
    public void testEmptyRange()
    {
        SearchRange [] range = SearchRange.getSearchRanges(100, 0, 1000, 50, false);
        assertEquals(0, range.length);
    }

    @Test
    public void testMoreThanMaxResultsRequested()
    {
        SearchRange [] range = SearchRange.getSearchRanges(1000, 10, 1000, 50, false);
        assertEquals(0, range.length);
    }

    @Test
    public void testGetTwoRangesFull()
    {
        SearchRange [] range = SearchRange.getSearchRanges(0, 100, 1000, 50, false);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(50, range[1].start);
        assertEquals(50, range[1].results);
    }

    @Test
    public void testGetTwoRangesUnderfull()
    {
        SearchRange [] range = SearchRange.getSearchRanges(0, 60, 1000, 50, false);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(50, range[1].start);
        assertEquals(10, range[1].results);
    }

    @Test
    public void testGetOneRangeStartAtNonZero()
    {
        SearchRange [] range = SearchRange.getSearchRanges(10, 20, 1000, 50, false);
        assertEquals(1, range.length);
        assertEquals(10, range[0].start);
        assertEquals(20, range[0].results);
    }

    @Test
    public void testGetTwoRangesStartAtNonZero()
    {
        SearchRange [] range = SearchRange.getSearchRanges(10, 60, 1000, 50, false);
        assertEquals(2, range.length);
        assertEquals(10, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(60, range[1].start);
        assertEquals(10, range[1].results);
    }

    @Test
    public void testPageModeEmptyRange()
    {
        SearchRange [] range = SearchRange.getSearchRanges(1, 0, 1000, 50, true);
        assertEquals(0, range.length);
    }

    @Test
    public void testPageModeMoreThanMaxResultsRequested()
    {
        SearchRange [] range = SearchRange.getSearchRanges(10, 10, 1000, 100, true);
        assertEquals(0, range.length);
    }

    @Test
    public void testPageModeGetTwoRangesFull()
    {
        SearchRange [] range = SearchRange.getSearchRanges(0, 100, 1000, 50, true);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(1, range[1].start);
        assertEquals(50, range[1].results);
    }

    @Test
    public void testPageModeGetTwoRangesUnderfull()
    {
        SearchRange [] range = SearchRange.getSearchRanges(0, 60, 1000, 50, true);
        assertEquals(2, range.length);
        assertEquals(0, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(1, range[1].start);
        assertEquals(10, range[1].results);
    }

    @Test
    public void testPageModeGetOneRangeStartAtNonZero()
    {
        SearchRange [] range = SearchRange.getSearchRanges(1, 20, 1000, 50, true);
        assertEquals(1, range.length);
        assertEquals(1, range[0].start);
        assertEquals(20, range[0].results);
    }

    @Test
    public void testPageModeGetTwoRangesStartAtNonZero()
    {
        SearchRange [] range = SearchRange.getSearchRanges(1, 60, 1000, 50, true);
        assertEquals(2, range.length);
        assertEquals(1, range[0].start);
        assertEquals(50, range[0].results);
        assertEquals(2, range[1].start);
        assertEquals(10, range[1].results);
    }
}
