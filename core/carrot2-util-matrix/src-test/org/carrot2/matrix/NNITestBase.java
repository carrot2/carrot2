package org.carrot2.matrix;


/**
 * Base class for tests of NNI-powered matrix operations.
 */
public class NNITestBase
{
    /**
     * Returns <code>true</code> if native LAPACK routines are available.
     */
    public boolean nativeLapackAvailable()
    {
        return NNIInterface.isNativeLapackAvailable();
    }
}
