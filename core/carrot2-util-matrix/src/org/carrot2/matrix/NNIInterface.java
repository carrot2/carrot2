
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import org.carrot2.matrix.nni.*;

/**
 * An interface to native matrix computation routines.
 */
public class NNIInterface
{
    private static final IBlasOperations blas;
    private static final ILapackOperations lapack;
    
    private static volatile boolean suppressNNI;

    static
    {
        ILapackOperations a = null;
        try
        {
            a = new LapackImpl();
        }
        catch (Throwable t)
        {
            // Not available, fall through.
        }
        lapack = a;
        
        IBlasOperations b = null;
        try
        {
            b = new BlasImpl();
        }
        catch (Throwable t)
        {
            // Not available, fall through.
        }
        blas = b;
    }

    private NNIInterface()
    {
        // No instance of this class
    }

    /**
     * Returns <code>true</code> if the native implementation of the BLAS package has been
     * successfully initialized.
     * 
     * @return <code>true</code> if the native implementation of the BLAS package has been
     *         successfully initialized.
     */
    public static boolean isNativeBlasAvailable()
    {
        return (suppressNNI ? false : blas != null);
    }

    /**
     * Returns <code>true</code> if the native implementation of the LAPACK package has
     * been successfully initialized.
     * 
     * @return <code>true</code> if the native implementation of the LAPACK package has
     *         been successfully initialized.
     */
    public static boolean isNativeLapackAvailable()
    {
        return (suppressNNI ? false : lapack != null);
    }

    /**
     * Temporarily suppresses native routines.
     * 
     * @param suppress <code>true</code> do disable native routines, <code>false</code> to
     *            enable native routines.
     */
    public static void suppressNNI(boolean suppress)
    {
        suppressNNI = suppress;
    }

    /**
     * Return the native-code implementation of certain math routines, if possible.  
     */
    public static IBlasOperations getBlas()
    {
        if (blas == null)
            throw new RuntimeException("No native blas available.");

        return blas;
    }
    
    /**
     * Return the native-code implementation of certain math routines, if possible.  
     */
    public static ILapackOperations getLapack()
    {
        if (lapack == null)
            throw new RuntimeException("No native blas available.");

        return lapack;
    }
}
