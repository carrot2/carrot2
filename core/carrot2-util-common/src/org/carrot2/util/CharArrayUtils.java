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
}
