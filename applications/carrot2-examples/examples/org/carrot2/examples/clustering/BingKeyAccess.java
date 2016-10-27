
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import org.carrot2.source.microsoft.v5.Bing5DocumentSource;

/**
 * All examples that use Bing API will acquire their key from this class.
 * <p>
 * You need your own API key to use Microsoft Bing (there is a free pool of request for
 * developers). Search for "Bing api" on Microsoft Marketplace or look at: <a
 * href="http://www.bing.com/toolbox/bingdeveloper/">
 * http://www.bing.com/toolbox/bingdeveloper/</a>.
 */
public class BingKeyAccess
{
    /**
     */
    public static String getKey()
    {
        // Try to acquire the key from system properties.
        final String key = System.getProperty(Bing5DocumentSource.SYSPROP_BING5_API);

        if (key == null)
        {
            System.out.println("WARNING! Empty Bing API V5 key,  pass it via -D"
                + Bing5DocumentSource.SYSPROP_BING5_API + "=...");

            // Return immediately as if nothing happened.
            System.exit(0);
        }
        
        return key;
    }
}
