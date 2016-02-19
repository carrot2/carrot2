
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

package org.carrot2.text.preprocessing.filter;

import java.util.*;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Filters out phrases that are not left complete.
 */
class LeftCompleteLabelFilter extends CompleteLabelFilterBase
{
    int [] createLcp(List<LabelIndexWithCodes> phraseCodes)
    {
        int [] lcpArray = new int [phraseCodes.size()];
        for (int i = 0; i < phraseCodes.size() - 1; i++)
        {
            int [] codes = phraseCodes.get(i).getCodes();
            int [] nextCodes = phraseCodes.get(i + 1).getCodes();

            int minLength = Math.min(codes.length, nextCodes.length);
            for (int j = 1; j <= minLength; j++)
            {
                if (codes[codes.length - j] != nextCodes[nextCodes.length - j])
                {
                    break;
                }

                lcpArray[i]++;
            }
        }

        lcpArray[lcpArray.length - 1] = -1;

        return lcpArray;
    }

    List<LabelIndexWithCodes> sortPhraseCodes(
        List<LabelIndexWithCodes> phrasesWithCodes)
    {
        final ArrayList<LabelIndexWithCodes> sortedPhraseCodes = Lists
            .newArrayList(phrasesWithCodes);
        Collections.sort(sortedPhraseCodes, new Comparator<LabelIndexWithCodes>()
        {
            public int compare(LabelIndexWithCodes o1, LabelIndexWithCodes o2)
            {
                int [] codesA = o1.getCodes();
                int [] codesB = o2.getCodes();

                int minLength = Math.min(codesA.length, codesB.length);
                for (int i = 1; i <= minLength; i++)
                {
                    if (codesA[codesA.length - i] < codesB[codesB.length - i])
                    {
                        return -1;
                    }
                    else if (codesA[codesA.length - i] > codesB[codesB.length - i])
                    {
                        return 1;
                    }
                }

                if (codesA.length < codesB.length)
                {
                    return -1;
                }
                else if (codesA.length > codesB.length)
                {
                    return 1;
                }

                return 0;
            }
        });

        return sortedPhraseCodes;
    }
}
