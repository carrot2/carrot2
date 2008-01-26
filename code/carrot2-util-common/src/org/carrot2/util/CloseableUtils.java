package org.carrot2.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Static methods for closing various stuff and ignoring exceptions not much can be done
 * about.
 */
public final class CloseableUtils
{

    private CloseableUtils()
    {
        // no instances.
    }

    /*
     * 
     */
    public static void closeIgnoringException(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                // Ignore.
            }
        }
    }
}
