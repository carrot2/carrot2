
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.carrot.filter.lingo.util.suffixarrays.wrapper;

import junit.framework.TestCase;


/**
 *
 */
public class SubstringComparatorTest extends TestCase {
    /** */

    /** DOCUMENT ME! */
    private static final StringIntWrapper emptyInputIntWrapper = new StringIntWrapper(
            "");

    /** DOCUMENT ME! */
    private static final Substring emptySubstring1 = new Substring(0, 0, 0, 0);

    /** DOCUMENT ME! */
    private static final Substring emptySubstring2 = new Substring(2, 2, 2, 0);

    /** DOCUMENT ME! */
    private static final int emptyOutput = 0;

    /**
     *
     */

    /** DOCUMENT ME! */
    private static final StringIntWrapper nonemptyInputIntWrapper = new StringIntWrapper(
            "to_be_be");

    /** DOCUMENT ME! */
    private static final Substring nonemptySubstring1 = new Substring(0, 2, 4, 0); // _be

    /** DOCUMENT ME! */
    private static final Substring nonemptySubstring2 = new Substring(2, 5, 7, 0); // _be

    /** DOCUMENT ME! */
    private static final Substring nonemptySubstring3 = new Substring(2, 1, 4, 0); // o_be

    /** DOCUMENT ME! */
    private static final Substring nonemptySubstring4 = new Substring(2, 5, 6, 0); // _

    /**
     *
     */
    public void testEmptyData() {
        assertEquals("empty data", emptyOutput,
            new SubstringComparator(emptyInputIntWrapper).compare(
                emptySubstring1, emptySubstring2));
    }

    /**
     *
     */
    public void testEquality() {
        // Reflexiveness
        assertEquals("equality", 0,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring1));

        assertEquals("equality", 0,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring2));
    }

    /**
     *
     */
    public void testInequality() {
        // Symmetry
        assertEquals("inequality",
            -(new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring3, nonemptySubstring1)),
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring1, nonemptySubstring3));

        assertEquals("inequality", 1,
            new SubstringComparator(nonemptyInputIntWrapper).compare(
                nonemptySubstring4, nonemptySubstring1));
    }
}
