
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


/**
 * Base class for tests of NNI-powered matrix operations.
 */
public final class NNITestAssumptions
{
    /**
     * Returns <code>true</code> if native LAPACK routines are available.
     */
    public static boolean nativeLapackAvailable()
    {
        return NNIInterface.isNativeLapackAvailable();
    }
}
