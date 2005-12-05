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
     * A utility method to load a set of words from a resource. Each line is
     * treated as one word. Lines beginning with '#' are ignored.
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
     * A utility method to load a set of words from a resource. Each line is
     * treated as one word. Lines beginning with '#' are ignored.
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

        logger.debug("Finished loading: " + resourceName);
        return set;
    }

    /**
     * @param resourceName
     * @param stream
     * @param convertToLowerCase
     * @return
     * @throws IOException
     */
    public static Set loadPhraseSet(String resourceName, InputStream stream,
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
            List words = new ArrayList();
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

                // Yes, I know StringTokenizer is slow. I use it here to avoid
                // extra dependencies. This method will most probably be called
                // only in the initialization phase anyway.
                StringTokenizer stringTokenizer = new StringTokenizer(line);

                while (stringTokenizer.hasMoreTokens())
                {
                    if (convertToLowerCase)
                    {
                        words.add(stringTokenizer.nextToken().toLowerCase());
                    }
                    else
                    {
                        words.add(stringTokenizer.nextToken());
                    }
                }

                set.add(words.toArray(new String [words.size()]));
                words.clear();
            }
        }
        finally
        {
            reader.close();
        }

        logger.debug("Finished loading: " + resourceName);

        return set;
    }

    /**
     * @param resourceName
     * @param stream
     * @param convertToLowerCase
     * @return
     * @throws IOException
     */
    public static Set loadParameterizedPhraseSet(String resourceName,
        InputStream stream, boolean convertToLowerCase, int parameterCount)
        throws IOException
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
            List words = new ArrayList();
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

                // Yes, I know StringTokenizer is slow. I use it here to avoid
                // extra dependencies. This method will most probably be called
                // only in the initialization phase anyway.
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                
                double [] parameters = new double [parameterCount];
                for (int i = 0; i < parameterCount; i++)
                {
                    parameters[i] = Double.parseDouble(stringTokenizer
                        .nextToken());
                }

                while (stringTokenizer.hasMoreTokens())
                {
                    if (convertToLowerCase)
                    {
                        words.add(stringTokenizer.nextToken().toLowerCase());
                    }
                    else
                    {
                        words.add(stringTokenizer.nextToken());
                    }
                }

                Object [] pair = new Object[2];
                pair[0] = parameters;
                pair[1] = words.toArray(new String [words.size()]); 
                
                set.add(pair);
                words.clear();
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
