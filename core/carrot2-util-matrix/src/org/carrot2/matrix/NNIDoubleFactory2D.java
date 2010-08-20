
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import org.apache.mahout.math.matrix.*;

/**
 * A factory for convenient construction of 2D matrices backed by the NNI interface. Use
 * the <code>nni</code> static field to obtain the instance of the NNI matrix factory. For
 * more details on using this factory see <code>org.apache.mahout.math.matrix.DoubleFactory2D</code>.
 */
@SuppressWarnings("deprecation")
public class NNIDoubleFactory2D extends DoubleFactory2D
{
    private static final long serialVersionUID = 1L;

    /**
     * A factory producing NNI native matrices
     */
    public static final DoubleFactory2D nni = new NNIDoubleFactory2D();

    public DoubleMatrix2D make(double [][] values)
    {
        if (this == nni)
        {
            return new NNIDenseDoubleMatrix2D(values);
        }
        else
        {
            return super.make(values);
        }
    }

    public DoubleMatrix2D make(int rows, int columns)
    {
        if (this == nni)
        {
            return new NNIDenseDoubleMatrix2D(rows, columns);
        }
        else
        {
            return super.make(rows, columns);
        }
    }

    /**
     * Converts a generic <code>DoubleMatrix2D</code> into an NNI-backed matrix.
     */
    public static DoubleMatrix2D asNNIMatrix(DoubleMatrix2D A)
    {
        return nni.make(A.rows(), A.columns()).assign(A);
    }
}
