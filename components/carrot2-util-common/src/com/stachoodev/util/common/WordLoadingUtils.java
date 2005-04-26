/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.util.common;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class WordLoadingUtils
{
    /** */
    private final static Logger logger = Logger
        .getLogger(WordLoadingUtils.class);

    
    /**
     * A utility method to load stop words from a resource.
     * 
     * @param resourceName
     * @param stream
     * @return
     * @throws IOException
     */
    public static Set loadWordSet(String resourceName, InputStream stream)
        throws IOException
    {
        return loadWordSet(resourceName, stream, false);
    }
    
    /**
     * A utility method to load stop words from a resource.
     * 
     * @param resourceName
     * @param stream
     * @return
     * @throws IOException
     */
    public static Set loadWordSet(String resourceName, InputStream stream,
        boolean convertToLowerCase) throws IOException
    {
        logger.debug("Loading word list for: " + resourceName);

        if (stream == null)
        {
            throw new IOException("Stream handle must not be null "
                + "(resource '" + resourceName + "' does not exist?)");
        }

        Set set = new HashSet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            stream, "UTF-8"));
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.startsWith("#"))
                    continue;
                if ("".equals(line))
                    continue;
                
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

        logger.debug("Finished loading: " + resourceName);
        return set;
    }
}
