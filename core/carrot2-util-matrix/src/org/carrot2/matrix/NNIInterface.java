
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

import nni.BLAS;
import nni.LAPACK;

/**
 * An interface to native matrix computation routines.
 */
public class NNIInterface
{
    /** Are native implementation available? */
    private static boolean nativeBlasAvailable;
    private static boolean nativeLapackAvailable;
    private static boolean suppressNNI;

    private NNIInterface()
    {
        // No instance of this class
    }

    static
    {
        // Try to initialize the native libraries
        try
        {
            BLAS.init();
            nativeBlasAvailable = true;
        }
        catch (Throwable t)
        {
            nativeBlasAvailable = false;
        }

        try
        {
            LAPACK.init();
            nativeLapackAvailable = true;
        }
        catch (Throwable t)
        {
            nativeLapackAvailable = false;
        }
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
        return (suppressNNI ? false : nativeBlasAvailable);
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
        return (suppressNNI ? false : nativeLapackAvailable);
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
}
