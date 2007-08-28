
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * 
 */
public final class WordLoadingUtils
{
    private final static Logger logger = Logger.getLogger(WordLoadingUtils.class);

    /**
     * A utility method to load a language resource. This method
     * uses locations scanned by {@link ResourceUtils}.
     */
    public static Set loadWordSet(String resourceName)
        throws IOException
    {
        final InputStream is = ResourceUtils.getFirst(resourceName, WordLoadingUtils.class);
        if (is == null) {
            throw new IOException("Resource could not be found: " + resourceName);
        }
        return loadWordSet(resourceName, is);
    }

    /**
     * A utility method to load a set of words from an input stream. Each line is
     * treated as one word. Lines beginning with '#' are ignored.
     */
    private static Set loadWordSet(String resourceName, InputStream stream)
        throws IOException
    {
        return loadWordSet(resourceName, stream, false);
    }

    /**
     * A utility method to load a set of words from a resource. Each line is
     * treated as one word. Lines beginning with '#' are ignored.
     * 
     * @param resourceName
     * @param stream
     * @throws IOException
     */
    private static Set loadWordSet(String resourceName, 
            InputStream stream, boolean convertToLowerCase) 
        throws IOException
    {
        if (stream == null)
        {
            throw new IOException("Stream handle must not be null "
                + "(resource '" + resourceName + "' does not exist?)");
        }

        logger.debug("Loading word set from: " + resourceName);

        final Set set = new HashSet();
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(stream, "UTF-8"));
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.startsWith("#"))
                {
                    continue;
                }

                if ("".equals(line))
                {
                    continue;
                }

                if (convertToLowerCase)
                {
                    set.add(line.toLowerCase());
                }
                else
                {
                    set.add(line);
                }
            }
        }
        finally
        {
            reader.close();
        }

        logger.info("Loaded: " + resourceName + " (" + set.size() + " entries)");
        return set;
    }
}
