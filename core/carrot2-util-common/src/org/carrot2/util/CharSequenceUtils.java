package org.carrot2.util;

/**
 * A number of useful methods for working with {@link CharSequence}s.
 */
public final class CharSequenceUtils
{
    /**
     * Converts a {@link CharSequence} into a <code>char []</code> array.
     */
    public static char [] toCharArray(CharSequence charSequence)
    {
        char [] array = new char [charSequence.length()];
        for (int i = 0; i < charSequence.length(); i++)
        {
            array[i] = charSequence.charAt(i);
        }
        return array;
    }

    private CharSequenceUtils()
    {
    }
}
