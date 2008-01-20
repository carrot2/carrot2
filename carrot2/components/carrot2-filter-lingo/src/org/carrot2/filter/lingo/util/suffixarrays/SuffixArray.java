
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
public class SuffixArray {
    /** */

    /** DOCUMENT ME! */
    protected int[] suffixArray;

    /**
     *
     */
    SuffixArray(int[] suffixArray) {
        this.suffixArray = suffixArray;
    }

    /**
     *
     */
    public int[] getSuffixArray() {
        return suffixArray;
    }

    /**
     *
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < suffixArray.length; i++) {
            stringBuffer.append(Integer.toString(suffixArray[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}
