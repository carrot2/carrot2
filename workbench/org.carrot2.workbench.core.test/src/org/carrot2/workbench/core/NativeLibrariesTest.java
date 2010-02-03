
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

package org.carrot2.workbench.core;

import junit.framework.TestCase;

import org.apache.lucene.util.Constants;
import org.carrot2.matrix.NNIInterface;

public class NativeLibrariesTest extends TestCase
{
    public void testNativeInterfaceLoaded()
    {
        // Can't use assumeTrue here because PDE uses junit3.x and it cannot be changed?
        if (Constants.WINDOWS && !Constants.JRE_IS_64BIT)
        {
            assertTrue("NNI BLAS not loaded.", 
                NNIInterface.isNativeBlasAvailable());
            assertTrue("NNI LAPACK not loaded.", 
                NNIInterface.isNativeLapackAvailable());
        }
    }
}
