/*
 * NNIDoubleFactory2D.java Created on 2004-06-12
 */
package cern.colt.matrix;

import cern.colt.matrix.impl.*;

/**
 * A factory for convenient construction of 2D matrices backed by the NNI
 * interface. Use the <code>nni</code> static field to obtain the instance of
 * the NNI matrix factory. For more details on using this factory see @link cern.colt.matrix.DoubleFactory2D.
 * 
 * @author stachoo
 */
public class NNIDoubleFactory2D extends DoubleFactory2D
{

    /**
     * A factory producing NNI native matrices
     */
    public static final DoubleFactory2D nni = new NNIDoubleFactory2D();

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleFactory2D#make(double[][])
     */
    public DoubleMatrix2D make(double[][] values)
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

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleFactory2D#make(int, int)
     */
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
}