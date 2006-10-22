
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * Various utility classes.
 *
 * @author stachoo
 * @author Dawid Weiss
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

        for (int i = 0; i < string.length(); i++)
        {
            if (Character.isUpperCase(string.charAt(i)))
            {
                capitalizedCount++;
            }
        }

        return (double) capitalizedCount / (double) string.length();
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

    /**
     * Capitalizes the string (first character only).
     */
    public static String capitalize(String string, Locale locale)
    {
        if (string.length() == 0)
        {
            return string;
        }

        return string.toUpperCase(locale).substring(0, 1)
            + string.substring(1).toLowerCase(locale);
    }


    /**
     * @param string
     * @param width
     */
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

    /**
     * Converts HTML and numeric entities in the text back 
     * to characters. 
     */
    public static String unescapeHtml(String str) {
        return StringEscapeUtils.unescapeHtml(str);
    }

    /**
     * Converts XML and numeric entities in the text back 
     * to characters. 
     */
    public static String unescapeXml(String str) {
        return StringEscapeUtils.unescapeXml(str);
    }

    /**
     * Removes html tags from a word. Html markup is for now defined
     * as sequences of characters between '<' and '>' characters
     * (even though it is an ugly heuristic).
     *
     * @param  word  The word to remove markup from.
     * @return A word with removed html tags.
     */
    public static String removeMarkup(String word) {
	    if (word == null) return null;

        final char[] chars = word.toCharArray();
        int i = 0;
        int j = 0;
        while (i < chars.length) {
            if (chars[i] == '<') {
                // skip until '>'
                while (i < chars.length && chars[i] != '>') {
                    i++;
                }
                if (i < chars.length) {
                    i++;
                }
                continue;
            }
            chars[j] = chars[i];
            i++;
            j++;
        }
        if (i == j) {
            return word;
        } else {
            return new String(chars, 0, j);
        }
    }    
    
    /**
     * Wraps the string at a given column. The wrapped lines can
     * be prefixed with another string.
     * <b>De-tabify the text first!</b>
     */
    public static String wrap( String text, int maxColumn, String prefix, String wrapAllowedAtChars, int maxGracefulWrapBacktrace )
    {
        if (prefix.length() >= maxColumn)
            throw new IllegalArgumentException("Prefix is longer than max. column.");

        StringBuffer output = new StringBuffer( text.length() );

        int columnPos = 0;
        int inputTextPosition;
        StringBuffer line = new StringBuffer();
        for (inputTextPosition = 0; inputTextPosition < text.length(); inputTextPosition++)
        {
            char chr = text.charAt(inputTextPosition);
            if (chr == '\r')
                continue;
            if (chr == '\n') {
                columnPos = 0;
                output.append( line );
                output.append('\n');
                line.setLength(0);
                continue;
            }
            while (true)
            {
                if (columnPos >= maxColumn) {
                    // try to break the line gracefully.
                    int i = line.length()-1;
                    int j = maxGracefulWrapBacktrace;
                    while (i>0 && j>0) {
                        if (wrapAllowedAtChars.indexOf(line.charAt(i)) != -1) {
                            // ok, break at this character.
                            break;
                        }
                        j--; i--;
                    }
                    if (j==0 || i==0) {
                        // force break.
                        columnPos = 0;
                        output.append(line);
                        output.append('\n');
                        if (prefix != null) {
                            output.append(prefix);
                            columnPos += prefix.length();
                        }
                        line.setLength(0);
                    }
                    else {
                        // break at this character.
                        output.append(line.substring(0, i+1));
                        output.append('\n');
                        columnPos = 0;
                        if (prefix != null) {
                            output.append(prefix);
                            columnPos += prefix.length();
                        }
                        String tmp = line.substring(i+1);
                        line.setLength(0);
                        line.append(tmp);
                        columnPos += line.length();
                    }
                } else {
                    break;
                }
            }

            line.append(chr);
            columnPos+=1;
        }
        // whatever remained. 
        output.append(line);

        return output.toString();
    }


    /**
     * Splits the <code>string</code> into parts delimited by
     * <code>delimiter</code> and stores the parts as {@link String}s in the
     * provided <code>list</code>. Note: substrings can be delimited by any
     * number of delimiter characters.
     * 
     * TODO: replace calls to this method with String.split() (JDK1.4)
     * 
     * @param string
     * @param delimiter
     * @param substrings
     * @return the <code>substrings</code> list, for convenience only
     */
    public static List split(String string, char delimiter, List substrings)
    {
        substrings.clear();
        string = string.trim();
    
        int i = 0;
        int j = string.indexOf(delimiter);
    
        while (j >= 0)
        {
            substrings.add(string.substring(i, j));
            
            i = j + 1;
            while (string.charAt(i) == delimiter)
            {
                i++;
            }
            
            j = string.indexOf(delimiter, i);
        }
    
        substrings.add(string.substring(i));
        
        return substrings;
    }
 
    /**
     * @param a
     * @param formatType as in the {@link java.text.MessageFormat}class
     */
    public static String toString(Double a, String formatType)
    {
        return java.text.MessageFormat.format("{0,number," + formatType + "}",
            new Object []
            { a });
    }
    
    /**
     * Returns <code>true</code> if given String is <code>null</code> or consits
     * of white space only. 
     * 
     * @param string
     * @return <code>true</code> if given String is <code>null</code> or consits
     * of white space only
     */
    public static boolean isBlank(String string)
    {
        return string == null || string.trim().length() == 0;
    }
}
