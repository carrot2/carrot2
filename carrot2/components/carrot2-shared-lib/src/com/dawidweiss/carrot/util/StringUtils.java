

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.util;


/**
 * Various utility classes.
 *
 * @author stachoo
 */
public class StringUtils
{
    /**
     * Calculates the ratio of capitalized characters to the length of the string.
     */
    public static double capitalizedRatio(String string)
    {
        if (string == null)
        {
            return 0;
        }

        int capitalizedCount = 0;
        char [] chars = string.toCharArray();

        for (int i = 0; i < chars.length; i++)
        {
            if (Character.isUpperCase(chars[i]))
            {
                capitalizedCount++;
            }
        }

        return (double) capitalizedCount / (double) chars.length;
    }


    /**
     * Capitalizes the string (first character only).
     */
    public static String capitalize(String string)
    {
        if (string.length() == 0)
        {
            return string;
        }

        return string.toUpperCase().substring(0, 1) + string.substring(1).toLowerCase();
    }


    public static String addLeftPadding(String string, int width)
    {
        if (string.length() >= width)
        {
            return string;
        }

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < (width - string.length()); i++)
        {
            result.append(" ");
        }

        result.append(string);

        return result.toString();
    }

    private static final String [] entities = 
    {
        "&nbsp;", " ", "&amp;", "&", "&quot;", "\"", "&lt;", "<", "&gt;", ">", "&bull;", " "
    };

    public static String entitiesToCharacters(String str, boolean exceptionOnUnrecognized)
    {
        char [] converted = str.toCharArray();

        int at = 0;
        int from = 0;

bigloop: 
        while (from < converted.length)
        {
            if (converted[from] == '&')
            {
                for (int i = 0; i < entities.length; i += 2)
                {
                    if (entities[i].regionMatches(0, str, from, entities[i].length()))
                    {
                        converted[at] = entities[i + 1].charAt(0);
                        at++;
                        from += entities[i].length();

                        continue bigloop;
                    }
                }

                if (exceptionOnUnrecognized)
                {
                    throw new IllegalArgumentException(
                        "Unrecognized entity: " + str.substring(from)
                    );
                }
            }

            converted[at] = converted[from];
            at++;
            from++;
        }

        return new String(converted, 0, at);
    }
}
