/*
 * NNIInterface.java Created on 2004-06-11
 */
package cern.colt.matrix.impl;

import nni.*;

/**
 * @author stachoo
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
        // Try to initialise the native libraries
        try
        {
            BLAS.init();
            nativeBlasAvailable = true;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            nativeBlasAvailable = false;
        }

        try
        {
            LAPACK.init();
            nativeLapackAvailable = true;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            nativeLapackAvailable = false;
        }
    }

    /**
     * Returns true if the native implementation of the BLAS package has been
     * successfully initialised.
     * 
     * @return true if the native implementation of the BLAS package has been
     *         successfully initialised.
     */
    public static boolean isNativeBlasAvailable()
    {
        return (suppressNNI ? false : nativeBlasAvailable);
    }

    /**
     * Returns true if the native implementation of the LAPACK package has been
     * successfully initialised.
     * 
     * @return true if the native implementation of the LAPACK package has been
     *         successfully initialised.
     */
    public static boolean isNativeLapackAvailable()
    {
        return (suppressNNI ? false : nativeLapackAvailable);
    }
    
    /**
     * Temporiarily suppresses NNI calls.
     * 
     * @param suppress
     */
    public static void suppressNNI(boolean suppress)
    {
        suppressNNI = suppress;
    }
}