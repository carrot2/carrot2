
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
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public final class WordLoadingUtils
{
    private final static Logger logger = Logger.getLogger(WordLoadingUtils.class);

    private final static String RESOURCE_PREFIX = "resources/";
    private final static String RESOURCE_ROOT = "/";

    /**
     * A utility method to load a language resource. The language resource
     * is seeked in the following locations (first found is returned):
     * <ul>
     *  <li><code>resources/*</code> - a resource loaded using current thread's
     *  context loader.</li>
     *  <li><code>resources/*</code> - a resource loaded using this class's
     *  loader.</li>
     * </ul> 
     */
    public static Set loadWordSet(String resourceName)
        throws IOException
    {
        InputStream is;

        is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(RESOURCE_PREFIX + resourceName);

        if (is == null) {
            is = WordLoadingUtils.class
                .getResourceAsStream(RESOURCE_PREFIX + resourceName);
        }

        if (is == null) {
            is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(RESOURCE_ROOT + RESOURCE_PREFIX + resourceName);
       }

        if (is == null) {
            is = WordLoadingUtils.class
                .getResourceAsStream(RESOURCE_ROOT + RESOURCE_PREFIX + resourceName);
        }

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
        logger.debug("Loading word list for: " + resourceName);

        if (stream == null)
        {
            throw new IOException("Stream handle must not be null "
                + "(resource '" + resourceName + "' does not exist?)");
        }

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

        logger.debug("Finished loading: " + resourceName);
        return set;
    }

    /**
     * @param resourceName
     * @param stream
     * @param convertToLowerCase
     * @throws IOException
     */
    private static Set loadPhraseSet(String resourceName, InputStream stream,
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
     * @throws IOException
     */
    private static Set loadParameterizedPhraseSet(String resourceName,
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


    /**
     * @param resourceName
     * @param stream
     * @param convertToLowerCase
     * @throws IOException
     */
    private static List loadPhraseSetsList(String resourceName,
        InputStream stream, boolean convertToLowerCase)
        throws IOException
    {
        logger.debug("Loading word list for: " + resourceName);

        if (stream == null)
        {
            throw new IOException("Stream handle must not be null "
                + "(resource '" + resourceName + "' does not exist?)");
        }

        List phraseSetsList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            stream, "UTF-8"));

        try
        {
            String line;
            List phrase = new ArrayList();
            while ((line = reader.readLine()) != null)
            {
                Set phraseSet = new HashSet();
                
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
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                
                while (stringTokenizer.hasMoreTokens())
                {
                    String phraseString;
                    if (convertToLowerCase)
                    {
                        phraseString = stringTokenizer.nextToken().toLowerCase();
                    }
                    else
                    {
                        phraseString = stringTokenizer.nextToken();
                    }
                    
                    StringTokenizer phraseTokenizer = new StringTokenizer(phraseString);
                    while (phraseTokenizer.hasMoreTokens())
                    {
                        phrase.add(phraseTokenizer.nextToken());
                    }
                    
                    phraseSet.add(phrase.toArray(new String [phrase.size()]));
                    phrase.clear();
                }
                
                phraseSetsList.add(phraseSet);
            }
        }
        finally
        {
            reader.close();
        }

        logger.debug("Finished loading: " + resourceName);

        return phraseSetsList;
    }
}
