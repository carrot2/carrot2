/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

import org.apache.commons.collections.primitives.*;

import cern.colt.matrix.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TdMatrixBuildingUtils
{
    /**
     * @param A
     * @return
     */
    public static IntList removeAllZeroColumns(DoubleMatrix2D A)
    {
        IntList removedColumnIndices = new ArrayIntList();

        for (int c = 0; c < A.columns(); c++)
        {
            if (A.viewColumn(c).cardinality() == 0)
            {
                removedColumnIndices.add(c);
            }
        }

        A = A.viewSelection(null, removedColumnIndices.toArray());

        return removedColumnIndices;
    }
}