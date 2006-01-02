
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.util.suffixarrays.wrapper;

import java.util.Comparator;


/**
 * Compares instances of the Substring class referring to given IntWrapper
 * instance.
 */
public class SubstringComparator implements Comparator {
    /** */

    /** DOCUMENT ME! */
    private int[] intData;

    /**
     *
     */
    public SubstringComparator(IntWrapper intWrapper) {
        this.intData = intWrapper.asIntArray();
    }

    /**
     * Note: Shorter strings are bigger !
     */
    public int compare(Object s1, Object s2) {
        if (!((s1 instanceof Substring) && (s2 instanceof Substring))) {
            throw new ClassCastException(s1.getClass().toString());
        }

        int s1From = ((Substring) s1).getFrom();
        int s1To = ((Substring) s1).getTo();
        int s2From = ((Substring) s2).getFrom();
        int s2To = ((Substring) s2).getTo();

        if (((s2To - s2From) == (s1To - s1From)) && ((s2To - s2From) == 0)) {
            return 0;
        }

        for (int i = 0;
                i < (((s2To - s2From) < (s1To - s1From)) ? (s2To - s2From)
                                                             : (s1To - s1From));
                i++) {
            if (intData[s1From + i] < intData[s2From + i]) {
                return -1;
            } else if (intData[s1From + i] > intData[s2From + i]) {
                return 1;
            }
        }

        if ((s2To - s2From) < (s1To - s1From)) {
            return -1;
        } else if ((s2To - s2From) > (s1To - s1From)) {
            return 1;
        } else {
            return 0;
        }
    }
}
