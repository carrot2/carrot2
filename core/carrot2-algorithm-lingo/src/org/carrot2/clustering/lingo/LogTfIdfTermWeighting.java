
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

package org.carrot2.clustering.lingo;

/**
 * Calculates term-document matrix element values based on Term Frequency.
 */
public class LogTfIdfTermWeighting implements TermWeighting
{
    public double calculateTermWeight(int termFrequency, int documentFrequency,
        int documentCount)
    {
        if (documentFrequency > 0)
        {
            return termFrequency * Math.log(documentCount / (double) documentFrequency);
        }
        else
        {
            return 0;
        }
    }
}
