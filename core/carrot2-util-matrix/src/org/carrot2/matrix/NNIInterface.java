
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

import org.carrot2.matrix.nni.BlasImpl;
import org.carrot2.matrix.nni.IBlasOperations;
import org.carrot2.matrix.nni.ILapackOperations;
import org.carrot2.matrix.nni.LapackImpl;
import org.slf4j.LoggerFactory;

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
        lapack = instantiateLapack();
        blas = instantiateBlas();
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
    
    /**
     * Instantiate Lapack (native).
     */
    // @AspectModified()
    private final static ILapackOperations instantiateLapack()
    {
        try
        {
            return new LapackImpl();
        }
        catch (Throwable t)
        {
            LoggerFactory.getLogger(NNIInterface.class).debug(
                "Failed to instantiate native LAPACK.", t);
        }
        return null;
    }

    /**
     * Instantiate blas (native).
     */
    // @AspectModified()
    private final static IBlasOperations instantiateBlas()
    {
        try
        {
            return new BlasImpl();
        }
        catch (Throwable t)
        {
            LoggerFactory.getLogger(NNIInterface.class).debug(
                "Failed to instantiate native BLAS.", t);
        }
        return null;
    }
}
