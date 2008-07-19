package org.carrot2.util;

/**
 * A number of useful methods for working with <code>char []</code> arrays.
 */
public class CharArrayUtils
{
    /**
     * In place to lower case conversion. In input array is returned for convenience.
     * 
     * @param array
     * @return
     */
    public static char [] inPlaceToLowerCase(char [] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = Character.toLowerCase(array[i]);
        }
        return array;
    }

    /**
     * To lower case conversion. A copy of the input array will be created.
     * 
     * @param array
     * @return
     */
    public static char [] toLowerCase(char [] array)
    {
        char [] lowerCase = new char [array.length];
        System.arraycopy(array, 0, lowerCase, 0, array.length);

        return inPlaceToLowerCase(lowerCase);
    }

    /**
     * Returns the ratio of capitalized letters in the string.
     */
    public static double capitalizedRatio(char [] string)
    {
        if (string.length == 0)
        {
            return 0;
        }

        int capitalized = 0;
        for (int i = 0; i < string.length; i++)
        {
            if (Character.isUpperCase(string[i]))
            {
                capitalized++;
            }
        }

        return capitalized / (double) string.length;
    }

    /**
     * Returns a capitalized copy of the string.
     */
    public static char [] capitalize(char [] string)
    {
        if (string.length == 0)
        {
            return string;
        }

        final char [] lowerCase = toLowerCase(string);
        lowerCase[0] = Character.toUpperCase(lowerCase[0]);

        return lowerCase;
    }
}
