
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.util.suffixarrays;


/**
 *
 */
public class LcpSuffixArray extends SuffixArray {
    /** */

    /** DOCUMENT ME! */
    protected int[] lcpArray;

    /**
     *
     */
    LcpSuffixArray(int[] suffixArray, int[] lcpArray) {
        super(suffixArray);

        this.lcpArray = lcpArray;
    }

    /**
     *
     */
    public int[] getLcpArray() {
        return lcpArray;
    }
}
