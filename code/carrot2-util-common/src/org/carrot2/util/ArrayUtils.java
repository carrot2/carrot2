/**
 * 
 */
package org.carrot2.util;

/**
 *
 */
public final class ArrayUtils
{
    private ArrayUtils()
    {
    }
    
    /**
     * Reverses an array in place. The reference to the input array is returned for 
     * convenience.
     */
    public static <T> T [] reverse(T [] array)
    {
        if (array != null) {
            T temp;
            
            for (int i = 0; i < (array.length / 2); i++)
            {
                temp = array[i];
                array[i] = array[array.length - i - 1];
                array[array.length - i - 1] = temp;
            }
        }
        
        return array;
    }
}
