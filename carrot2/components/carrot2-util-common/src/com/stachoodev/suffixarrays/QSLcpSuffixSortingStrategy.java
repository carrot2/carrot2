
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

package com.stachoodev.suffixarrays;

import com.stachoodev.suffixarrays.wrapper.*;

/**
 * A naive implementation of the LcpSuffixSortingStrategy interface employing
 * Quick Sort for suffix sorting and a home-grown algorithm for LCP computation.
 * The complexity of suffix-sorting is O(N^2logN) and that of LCP finding is
 * O(N^2), which does not present any substantial difference in efficiency when
 * applied to small-scale problems, such as search results clustering.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class QSLcpSuffixSortingStrategy implements LcpSuffixSortingStrategy
{
    /**
     * LCP suffix-sorts the input data using the Quick Sort algorithm.
     */
    public LcpSuffixArray lcpSuffixSort(IntWrapper intWrapper)
    {
        int [] suffixData = intWrapper.asIntArray();
        int [] suffixArray = new int [suffixData.length - 1];
        int [] lcpArray = new int [suffixData.length];

        SuffixArraysUtils.qsSuffixSort(suffixArray, suffixData);

        if (intWrapper instanceof TypeAwareIntWrapper)
        {
            SuffixArraysUtils.naiveLcp(suffixArray, lcpArray, suffixData, 0,
                (TypeAwareIntWrapper) intWrapper);
        }
        else
        {
            SuffixArraysUtils.naiveLcp(suffixArray, lcpArray, suffixData);
        }

        return new LcpSuffixArray(suffixArray, lcpArray);
    }
}