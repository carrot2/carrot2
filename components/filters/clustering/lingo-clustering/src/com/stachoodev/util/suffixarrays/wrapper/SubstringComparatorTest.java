

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.util.suffixarrays.wrapper;


import junit.framework.TestCase;


/**
 *
 */
public class SubstringComparatorTest
    extends TestCase
{
    /** */
    private static final StringIntWrapper emptyInputIntWrapper = new StringIntWrapper("");
    private static final Substring emptySubstring1 = new Substring(0, 0, 0, 0);
    private static final Substring emptySubstring2 = new Substring(2, 2, 2, 0);
    private static final int emptyOutput = 0;

    /**
     *
     */
    private static final StringIntWrapper nonemptyInputIntWrapper = new StringIntWrapper(
            "to_be_be"
        );
    private static final Substring nonemptySubstring1 = new Substring(0, 2, 4, 0); // _be
    private static final Substring nonemptySubstring2 = new Substring(2, 5, 7, 0); // _be
    private static final Substring nonemptySubstring3 = new Substring(2, 1, 4, 0); // o_be
    private static final Substring nonemptySubstring4 = new Substring(2, 5, 6, 0); // _

    /**
     *
     */
    public void testEmptyData()
    {
        assertEquals(
            "empty data", emptyOutput,
            new SubstringComparator(emptyInputIntWrapper).compare(emptySubstring1, emptySubstring2)
        );
    }


    /**
     *
     */
    public void testEquality()
    {
        // Reflexiveness
        assertEquals(
            "equality", 0,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring1
            )
        );

        assertEquals(
            "equality", 0,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring2
            )
        );
    }


    /**
     *
     */
    public void testInequality()
    {
        // Symmetry
        assertEquals(
            "inequality",
            -(new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring3, nonemptySubstring1
            )),
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring3
            )
        );

        assertEquals(
            "inequality", 1,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring4, nonemptySubstring1
            )
        );
    }
}
