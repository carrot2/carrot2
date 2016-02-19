
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

package org.carrot2.util;

/**
 * A number of useful methods for working with <code>char []</code> arrays.
 */
public class CharArrayUtils
{
    public final static char [] EMPTY_ARRAY = new char [0];

    /**
     * In place to lower case conversion. In input array is returned for convenience.
     */
    public static char [] toLowerCaseInPlace(char [] array)
    {
        for (int i = array.length; --i >= 0;)
        {
            array[i] = Character.toLowerCase(array[i]);
        }

        return array;
    }

    /**
     * To lower case conversion. A copy of the input array will be created.
     */
    public static char [] toLowerCaseCopy(char [] array)
    {
        return toLowerCaseInPlace((char []) array.clone());
    }

    /**
     * Computes and returns the ratio of capitalized 
     * letters in the string to the numbers of all letters.
     */
    public static float getCapitalizedRatio(char [] string)
    {
        final int len = string.length;

        if (len == 0) return 0;

        int capitalized = 0;
        for (int i = len; --i >= 0;)
        {
            if (Character.isUpperCase(string[i]))
                capitalized++;
        }

        return capitalized / (float) len;
    }

    /**
     * Returns <code>true</code> if the input array contains any capitalized
     * characters.
     */
    public static boolean hasCapitalizedLetters(char [] string)
    {
        for (int i = string.length; --i >= 0;)
        {
            if (Character.isUpperCase(string[i]))
                return true;
        }

        return false;
    }

    /**
     * Returns a capitalized copy of the input character array.
     */
    public static char [] toCapitalizedCopy(char [] string)
    {
        final int len = string.length;

        if (len == 0) return string;

        final char [] lowerCase = toLowerCaseCopy(string);
        lowerCase[0] = Character.toUpperCase(lowerCase[0]);

        return lowerCase;
    }

    /**
     * Convert to lower case (character-by-character) and save the result
     * into <code>buffer</code>. 
     * 
     * @param word The word to be converted to lower case.
     * @param buffer The buffer where the result should be saved.
     * @return Returns <code>true</code> if at least one character was changed
     * between <code>word</code> and <code>buffer</code>. <code>false</code> indicates
     * an identical copy.
     * @throws AssertionError If <code>buffer</code> is smaller than <code>word</code>.
     */
    public static boolean toLowerCase(char [] word, char [] buffer)
    {
        return toLowerCase(word, buffer, 0, word.length);
    }
    
    /**
     * Convert to lower case (character-by-character) and save the result
     * into <code>buffer</code>. The buffer must have at least <code>length</code>
     * characters.
     * 
     * @param word The word to be converted to lower case.
     * @param buffer The buffer where the result should be saved.
     * @param start the index in the <code>word</code> at which to start
     * @param length the number of characters from <code>word</code> to process
     * @return Returns <code>true</code> if at least one character was changed
     * between <code>word</code> and <code>buffer</code>. <code>false</code> indicates
     * an identical copy.
     * @throws AssertionError If <code>buffer</code> is smaller than <code>length</code>.
     * @throws AssertionError If <code>start + length</code> is smaller than the length <code>word</code>.
     */
    public static boolean toLowerCase(char [] word, char [] buffer, int start, int length)
    {
        assert buffer.length >= length: "Buffer too small.";
        assert start + length <= word.length : "Word too short.";
        assert start >= 0 : "Start must be >= 0";
        
        boolean different = false;
        char in, out;
        for (int i = length; --i >= 0;)
        {
            buffer[i] = out = Character.toLowerCase(in = word[i + start]);
            different |= (in != out);
        }
        return different;
    }
}
