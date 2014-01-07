
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import org.carrot2.source.microsoft.Bing3DocumentSource;

/**
 * All examples that use Bing will acquire their key from this class.
 * <p>
 * You need your own API key to use Microsoft Bing (there is a free pool of request for
 * developers). Search for "bing api" on Microsoft Marketplace or look at: <a
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
        final String key = System.getProperty(Bing3DocumentSource.SYSPROP_BING3_API);

        if (key == null)
        {
            System.out.println("WARNING! Empty Bing API key,  pass it via -D"
                + Bing3DocumentSource.SYSPROP_BING3_API + "=...");

            // Return immediately as if nothing happened.
            System.exit(0);
        }
        
        return key;
    }
}
