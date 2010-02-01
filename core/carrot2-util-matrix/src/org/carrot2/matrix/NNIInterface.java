
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

import org.carrot2.matrix.nni.LapackBridge;
import org.carrot2.matrix.nni.NativeOps;

/**
 * An interface to native matrix computation routines.
 */
public class NNIInterface
{
    private static final NativeOps nniImpl;
    private static volatile boolean suppressNNI;
    
    static
    {
        NativeOps nni = null;
        try
        {
            nni = new LapackBridge();
        }
        catch (Throwable t)
        {
            // Not available, fall through.
        }
        nniImpl = nni;
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
        return (suppressNNI ? false : nniImpl != null);
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
        return (suppressNNI ? false : nniImpl != null);
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
    public static NativeOps getBridge()
    {
        if (nniImpl == null)
            throw new RuntimeException("Call to getBridge() when NNI not available.");

        return nniImpl;
    }
}
